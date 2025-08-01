package io.cpk.be.basic.repository

import io.cpk.be.basic.dto.OrgConfig
import io.cpk.be.basic.entity.OrgType
import io.cpk.be.config.TestConfig
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager
import org.springframework.context.annotation.Import
import org.springframework.data.domain.PageRequest
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Unit tests for OrgTypeRepository
 */
@DataJpaTest
@Import(TestConfig::class)
class OrgTypeRepositoryTest {

    @Autowired private lateinit var entityManager: TestEntityManager

    @Autowired private lateinit var orgTypeRepository: OrgTypeRepository

    @Test
    fun `should save and find org type`() {
        // Given
        val config = OrgConfig(mapOf("key" to "value"))
        val orgType = OrgType(
            name = "test-org-type",
            accessRight = listOf("read", "write"),
            orgConfigs = config
        )

        // When
        val savedOrgType = entityManager.persistAndFlush(orgType)
        val found = orgTypeRepository.findById(savedOrgType.name)

        // Then
        assertTrue(found.isPresent)
        val foundOrgType = found.get()
        assertEquals(savedOrgType.name, foundOrgType.name)
        assertEquals(listOf("read", "write"), foundOrgType.accessRight)
        assertEquals(config.toMap(), foundOrgType.orgConfigs.toMap())
    }

    @Test
    fun `should find all org types`() {
        // Given
        val config1 = OrgConfig(mapOf("key1" to "value1"))
        val config2 = OrgConfig(mapOf("key2" to "value2"))
        
        val orgType1 = OrgType(
            name = "org-type-1",
            accessRight = listOf("read"),
            orgConfigs = config1
        )
        val orgType2 = OrgType(
            name = "org-type-2",
            accessRight = listOf("write"),
            orgConfigs = config2
        )

        entityManager.persistAndFlush(orgType1)
        entityManager.persistAndFlush(orgType2)

        // When
        val orgTypes = orgTypeRepository.findAll()

        // Then
        assertTrue(orgTypes.size >= 2)
        assertTrue(orgTypes.any { it.name == "org-type-1" })
        assertTrue(orgTypes.any { it.name == "org-type-2" })
    }

    @Test
    fun `should find org types by name containing with pagination`() {
        // Given
        val config1 = OrgConfig(mapOf("admin" to "true"))
        val config2 = OrgConfig(mapOf("super" to "true"))
        val config3 = OrgConfig(mapOf("user" to "true"))
        
        val orgType1 = OrgType(
            name = "admin-type",
            accessRight = listOf("admin"),
            orgConfigs = config1
        )
        val orgType2 = OrgType(
            name = "super-admin-type",
            accessRight = listOf("admin", "super"),
            orgConfigs = config2
        )
        val orgType3 = OrgType(
            name = "user-type",
            accessRight = listOf("read"),
            orgConfigs = config3
        )

        entityManager.persistAndFlush(orgType1)
        entityManager.persistAndFlush(orgType2)
        entityManager.persistAndFlush(orgType3)

        // When
        val pageable = PageRequest.of(0, 10)
        val result = orgTypeRepository.findAllByNameContaining("admin", pageable)

        // Then
        assertEquals(2, result.totalElements)
        assertTrue(result.content.any { it.name == "admin-type" })
        assertTrue(result.content.any { it.name == "super-admin-type" })
        assertTrue(result.content.none { it.name == "user-type" })
    }

    @Test
    fun `should delete org type`() {
        // Given
        val config = OrgConfig(mapOf("delete" to "true"))
        val orgType = OrgType(
            name = "org-type-to-delete",
            accessRight = listOf("delete"),
            orgConfigs = config
        )

        val savedOrgType = entityManager.persistAndFlush(orgType)
        assertTrue(orgTypeRepository.findById(savedOrgType.name).isPresent)

        // When
        orgTypeRepository.deleteById(savedOrgType.name)
        entityManager.flush()

        // Then
        assertTrue(orgTypeRepository.findById(savedOrgType.name).isEmpty)
    }

    @Test
    fun `should update org type`() {
        // Given
        val config1 = OrgConfig(mapOf("read" to "true"))
        val config2 = OrgConfig(mapOf("read" to "true", "write" to "true"))
        
        val orgType = OrgType(
            name = "org-type-to-update",
            accessRight = listOf("read"),
            orgConfigs = config1
        )

        val savedOrgType = entityManager.persistAndFlush(orgType)

        // When
        val updatedOrgType = savedOrgType.copy(
            accessRight = listOf("read", "write"),
            orgConfigs = config2
        )
        orgTypeRepository.save(updatedOrgType)
        entityManager.flush()

        // Then
        val updated = orgTypeRepository.findById(savedOrgType.name).get()
        assertEquals("org-type-to-update", updated.name)
        assertEquals(listOf("read", "write"), updated.accessRight)
        assertEquals(config2.toMap(), updated.orgConfigs.toMap())
    }
}