package io.cpk.be.basic.repository

import io.cpk.be.basic.entity.AppUser
import io.cpk.be.basic.entity.Org
import io.cpk.be.basic.entity.OrgType
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
 * Unit tests for AppUserRepository
 */
@DataJpaTest
@Import(TestConfig::class)
class AppUserRepositoryTest {

    @Autowired private lateinit var entityManager: TestEntityManager

    @Autowired private lateinit var appUserRepository: AppUserRepository
    
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
    fun `should save and find app user`() {
        // Given
        val appUser = AppUser(
            id = "user123",
            username = "testuser",
            fullname = "Test User",
            email = "test@example.com",
            orgId = testOrg.id!!,
            password = "encoded_password",
            orgAdmin = false
        )

        // When
        val savedAppUser = entityManager.persistAndFlush(appUser)
        val found = appUserRepository.findById(savedAppUser.id)

        // Then
        assertTrue(found.isPresent)
        val foundAppUser = found.get()
        assertEquals(savedAppUser.id, foundAppUser.id)
        assertEquals("testuser", foundAppUser.username)
        assertEquals("Test User", foundAppUser.fullname)
        assertEquals("test@example.com", foundAppUser.email)
        assertEquals(testOrg.id, foundAppUser.orgId)
        assertEquals("encoded_password", foundAppUser.password)
        assertEquals(false, foundAppUser.orgAdmin)
    }

    @Test
    fun `should find app user by username`() {
        // Given
        val appUser = AppUser(
            id = "user123",
            username = "testuser",
            fullname = "Test User",
            email = "test@example.com",
            orgId = testOrg.id!!,
            password = "encoded_password",
            orgAdmin = false
        )
        entityManager.persistAndFlush(appUser)

        // When
        val found = appUserRepository.findByUsername("testuser")

        // Then
        assertTrue(found.isPresent)
        val foundAppUser = found.get()
        assertEquals("user123", foundAppUser.id)
        assertEquals("testuser", foundAppUser.username)
        assertEquals("Test User", foundAppUser.fullname)
    }

    @Test
    fun `should find all app users by org id with pagination`() {
        // Given
        val appUser1 = AppUser(
            id = "user1",
            username = "user1",
            fullname = "User One",
            email = "user1@example.com",
            orgId = testOrg.id!!,
            password = "encoded_password1",
            orgAdmin = false
        )
        val appUser2 = AppUser(
            id = "user2",
            username = "user2",
            fullname = "User Two",
            email = "user2@example.com",
            orgId = testOrg.id!!,
            password = "encoded_password2",
            orgAdmin = true
        )
        val appUser3 = AppUser(
            id = "user3",
            username = "user3",
            fullname = "User Three",
            email = "user3@example.com",
            orgId = 999, // Different org
            password = "encoded_password3",
            orgAdmin = false
        )

        entityManager.persistAndFlush(appUser1)
        entityManager.persistAndFlush(appUser2)
        entityManager.persistAndFlush(appUser3)

        // When
        val pageable = PageRequest.of(0, 10)
        val result = appUserRepository.findAllByOrgId(testOrg.id!!, pageable)

        // Then
        assertEquals(2, result.totalElements)
        assertTrue(result.content.any { it.id == "user1" })
        assertTrue(result.content.any { it.id == "user2" })
        assertTrue(result.content.none { it.id == "user3" })
    }

    @Test
    fun `should find all app users by org id and fullname containing with pagination`() {
        // Given
        val appUser1 = AppUser(
            id = "user1",
            username = "user1",
            fullname = "John Smith",
            email = "john@example.com",
            orgId = testOrg.id!!,
            password = "encoded_password1",
            orgAdmin = false
        )
        val appUser2 = AppUser(
            id = "user2",
            username = "user2",
            fullname = "Jane Smith",
            email = "jane@example.com",
            orgId = testOrg.id!!,
            password = "encoded_password2",
            orgAdmin = true
        )
        val appUser3 = AppUser(
            id = "user3",
            username = "user3",
            fullname = "Bob Johnson",
            email = "bob@example.com",
            orgId = testOrg.id!!,
            password = "encoded_password3",
            orgAdmin = false
        )

        entityManager.persistAndFlush(appUser1)
        entityManager.persistAndFlush(appUser2)
        entityManager.persistAndFlush(appUser3)

        // When
        val pageable = PageRequest.of(0, 10)
        val result = appUserRepository.findAllByOrgIdAndFullnameContaining(testOrg.id!!, "Smith", pageable)

        // Then
        assertEquals(2, result.totalElements)
        assertTrue(result.content.any { it.id == "user1" })
        assertTrue(result.content.any { it.id == "user2" })
        assertTrue(result.content.none { it.id == "user3" })
    }

    @Test
    fun `should delete app user`() {
        // Given
        val appUser = AppUser(
            id = "user123",
            username = "testuser",
            fullname = "Test User",
            email = "test@example.com",
            orgId = testOrg.id!!,
            password = "encoded_password",
            orgAdmin = false
        )

        val savedAppUser = entityManager.persistAndFlush(appUser)
        assertTrue(appUserRepository.findById(savedAppUser.id).isPresent)

        // When
        appUserRepository.deleteById(savedAppUser.id)
        entityManager.flush()

        // Then
        assertTrue(appUserRepository.findById(savedAppUser.id).isEmpty)
    }
}