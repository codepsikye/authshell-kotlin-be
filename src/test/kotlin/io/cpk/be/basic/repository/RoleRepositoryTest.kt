package io.cpk.be.basic.repository

import io.cpk.be.basic.entity.Org
import io.cpk.be.basic.entity.OrgType
import io.cpk.be.basic.entity.Role
import io.cpk.be.config.TestConfig
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager
import org.springframework.context.annotation.Import
import org.springframework.data.domain.PageRequest
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Unit tests for RoleRepository
 */
@DataJpaTest
@Import(TestConfig::class)
class RoleRepositoryTest {

    @Autowired private lateinit var entityManager: TestEntityManager

    @Autowired private lateinit var roleRepository: RoleRepository
    
    private lateinit var testOrgType: OrgType
    private lateinit var testOrg: Org
    
    @BeforeEach
    fun setUp() {
        // Create and persist OrgType
        testOrgType = OrgType(
            name = "test-org-type",
            accessRight = listOf("read", "write"),
            orgConfigs = mapOf("key" to "value")
        )
        entityManager.persistAndFlush(testOrgType)
        
        // Create and persist Org
        testOrg = Org(
            name = "Test Organization",
            address = "123 Org St",
            phone = "555-0000",
            city = "Test City",
            country = "Test Country",
            notes = "Test Notes",
            orgTypeName = testOrgType.name
        )
        entityManager.persistAndFlush(testOrg)
    }

    @Test
    fun `should save and find role`() {
        // Given
        val role = Role(
            orgId = testOrg.id!!,
            name = "admin",
            accessRight = listOf("read", "write", "delete")
        )

        // When
        val savedRole = entityManager.persistAndFlush(role)
        val found = roleRepository.findById(Role.RoleId(savedRole.orgId, savedRole.name))

        // Then
        assertTrue(found.isPresent)
        val foundRole = found.get()
        assertEquals(savedRole.orgId, foundRole.orgId)
        assertEquals(savedRole.name, foundRole.name)
        assertEquals(listOf("read", "write", "delete"), foundRole.accessRight)
    }

    @Test
    fun `should find all roles`() {
        // Given
        val role1 = Role(
            orgId = testOrg.id!!,
            name = "admin",
            accessRight = listOf("read", "write", "delete")
        )
        val role2 = Role(
            orgId = testOrg.id!!,
            name = "user",
            accessRight = listOf("read")
        )

        entityManager.persistAndFlush(role1)
        entityManager.persistAndFlush(role2)

        // When
        val roles = roleRepository.findAll()

        // Then
        assertTrue(roles.size >= 2)
        assertTrue(roles.any { it.orgId == testOrg.id && it.name == "admin" })
        assertTrue(roles.any { it.orgId == testOrg.id && it.name == "user" })
    }

    @Test
    fun `should find all roles by org id with pagination`() {
        // Given
        val role1 = Role(
            orgId = testOrg.id!!,
            name = "admin",
            accessRight = listOf("read", "write", "delete")
        )
        val role2 = Role(
            orgId = testOrg.id!!,
            name = "user",
            accessRight = listOf("read")
        )
        
        // Create another org and role
        val anotherOrg = Org(
            name = "Another Organization",
            address = "456 Another St",
            phone = "555-9999",
            city = "Another City",
            country = "Another Country",
            notes = "Another Notes",
            orgTypeName = testOrgType.name
        )
        entityManager.persistAndFlush(anotherOrg)
        
        val role3 = Role(
            orgId = anotherOrg.id!!,
            name = "manager",
            accessRight = listOf("read", "write")
        )

        entityManager.persistAndFlush(role1)
        entityManager.persistAndFlush(role2)
        entityManager.persistAndFlush(role3)

        // When
        val pageable = PageRequest.of(0, 10)
        val result = roleRepository.findAllByOrgId(testOrg.id!!, pageable)

        // Then
        assertEquals(2, result.totalElements)
        assertTrue(result.content.any { it.name == "admin" })
        assertTrue(result.content.any { it.name == "user" })
        assertTrue(result.content.none { it.name == "manager" })
    }

    @Test
    fun `should find all roles by org id and name containing with pagination`() {
        // Given
        val role1 = Role(
            orgId = testOrg.id!!,
            name = "admin",
            accessRight = listOf("read", "write", "delete")
        )
        val role2 = Role(
            orgId = testOrg.id!!,
            name = "super-admin",
            accessRight = listOf("read", "write", "delete", "super")
        )
        val role3 = Role(
            orgId = testOrg.id!!,
            name = "user",
            accessRight = listOf("read")
        )

        entityManager.persistAndFlush(role1)
        entityManager.persistAndFlush(role2)
        entityManager.persistAndFlush(role3)

        // When
        val pageable = PageRequest.of(0, 10)
        val result = roleRepository.findAllByOrgIdAndNameContaining(testOrg.id!!, "admin", pageable)

        // Then
        assertEquals(2, result.totalElements)
        assertTrue(result.content.any { it.name == "admin" })
        assertTrue(result.content.any { it.name == "super-admin" })
        assertTrue(result.content.none { it.name == "user" })
    }

    @Test
    fun `should delete role`() {
        // Given
        val role = Role(
            orgId = testOrg.id!!,
            name = "role-to-delete",
            accessRight = listOf("delete")
        )

        val savedRole = entityManager.persistAndFlush(role)
        assertTrue(roleRepository.findById(Role.RoleId(savedRole.orgId, savedRole.name)).isPresent)

        // When
        roleRepository.deleteById(Role.RoleId(savedRole.orgId, savedRole.name))
        entityManager.flush()

        // Then
        assertTrue(roleRepository.findById(Role.RoleId(savedRole.orgId, savedRole.name)).isEmpty)
    }

    @Test
    fun `should update role`() {
        // Given
        val role = Role(
            orgId = testOrg.id!!,
            name = "role-to-update",
            accessRight = listOf("read")
        )

        val savedRole = entityManager.persistAndFlush(role)

        // When
        val updatedRole = savedRole.copy(
            accessRight = listOf("read", "write", "update")
        )
        roleRepository.save(updatedRole)
        entityManager.flush()

        // Then
        val updated = roleRepository.findById(Role.RoleId(savedRole.orgId, savedRole.name)).get()
        assertEquals(testOrg.id, updated.orgId)
        assertEquals("role-to-update", updated.name)
        assertEquals(listOf("read", "write", "update"), updated.accessRight)
    }
}