package io.cpk.be.basic.repository

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
 * Unit tests for OrgRepository
 */
@DataJpaTest
@Import(TestConfig::class)
class OrgRepositoryTest {

    @Autowired private lateinit var entityManager: TestEntityManager

    @Autowired private lateinit var orgRepository: OrgRepository
    
    private lateinit var testOrgType: OrgType
    
    @BeforeEach
    fun setUp() {
        // Create and persist OrgType
        testOrgType = OrgType(
            name = "test-org-type",
            accessRight = listOf("read", "write"),
            orgConfigs = mapOf("key" to "value")
        )
        entityManager.persistAndFlush(testOrgType)
    }

    @Test
    fun `should save and find org`() {
        // Given
        val org = Org(
            name = "Test Organization",
            address = "123 Org St",
            phone = "555-0000",
            city = "Test City",
            country = "Test Country",
            notes = "Test Notes",
            orgTypeName = testOrgType.name
        )

        // When
        val savedOrg = entityManager.persistAndFlush(org)
        val found = orgRepository.findById(savedOrg.id!!)

        // Then
        assertTrue(found.isPresent)
        val foundOrg = found.get()
        assertEquals(savedOrg.id, foundOrg.id)
        assertEquals("Test Organization", foundOrg.name)
        assertEquals("123 Org St", foundOrg.address)
        assertEquals("555-0000", foundOrg.phone)
        assertEquals("Test City", foundOrg.city)
        assertEquals("Test Country", foundOrg.country)
        assertEquals("Test Notes", foundOrg.notes)
        assertEquals(testOrgType.name, foundOrg.orgTypeName)
    }

    @Test
    fun `should find all orgs`() {
        // Given
        val org1 = Org(
            name = "Organization One",
            address = "123 First St",
            phone = "555-1111",
            city = "First City",
            country = "First Country",
            notes = "First Notes",
            orgTypeName = testOrgType.name
        )
        val org2 = Org(
            name = "Organization Two",
            address = "456 Second St",
            phone = "555-2222",
            city = "Second City",
            country = "Second Country",
            notes = "Second Notes",
            orgTypeName = testOrgType.name
        )

        entityManager.persistAndFlush(org1)
        entityManager.persistAndFlush(org2)

        // When
        val orgs = orgRepository.findAll()

        // Then
        assertTrue(orgs.size >= 2)
        assertTrue(orgs.any { it.name == "Organization One" })
        assertTrue(orgs.any { it.name == "Organization Two" })
    }

    @Test
    fun `should find orgs by name containing with pagination`() {
        // Given
        val org1 = Org(
            name = "Tech Company",
            address = "123 Tech St",
            phone = "555-1111",
            city = "Tech City",
            country = "Tech Country",
            notes = "Tech Notes",
            orgTypeName = testOrgType.name
        )
        val org2 = Org(
            name = "Tech Startup",
            address = "456 Startup St",
            phone = "555-2222",
            city = "Startup City",
            country = "Startup Country",
            notes = "Startup Notes",
            orgTypeName = testOrgType.name
        )
        val org3 = Org(
            name = "Finance Corp",
            address = "789 Finance St",
            phone = "555-3333",
            city = "Finance City",
            country = "Finance Country",
            notes = "Finance Notes",
            orgTypeName = testOrgType.name
        )

        entityManager.persistAndFlush(org1)
        entityManager.persistAndFlush(org2)
        entityManager.persistAndFlush(org3)

        // When
        val pageable = PageRequest.of(0, 10)
        val result = orgRepository.findAllByNameContaining("Tech", pageable)

        // Then
        assertEquals(2, result.totalElements)
        assertTrue(result.content.any { it.name == "Tech Company" })
        assertTrue(result.content.any { it.name == "Tech Startup" })
        assertTrue(result.content.none { it.name == "Finance Corp" })
    }

    @Test
    fun `should delete org`() {
        // Given
        val org = Org(
            name = "Org to Delete",
            address = "123 Delete St",
            phone = "555-3333",
            city = "Delete City",
            country = "Delete Country",
            notes = "Delete Notes",
            orgTypeName = testOrgType.name
        )

        val savedOrg = entityManager.persistAndFlush(org)
        assertTrue(orgRepository.findById(savedOrg.id!!).isPresent)

        // When
        orgRepository.deleteById(savedOrg.id!!)
        entityManager.flush()

        // Then
        assertTrue(orgRepository.findById(savedOrg.id!!).isEmpty)
    }

    @Test
    fun `should update org`() {
        // Given
        val org = Org(
            name = "Org to Update",
            address = "123 Update St",
            phone = "555-4444",
            city = "Update City",
            country = "Update Country",
            notes = "Update Notes",
            orgTypeName = testOrgType.name
        )

        val savedOrg = entityManager.persistAndFlush(org)

        // When
        val foundOrg = orgRepository.findById(savedOrg.id!!).get()
        val updatedOrg = foundOrg.copy(
            name = "Updated Org",
            address = "456 Updated St",
            phone = "555-5555",
            city = "Updated City",
            country = "Updated Country",
            notes = "Updated Notes"
        )
        orgRepository.save(updatedOrg)
        entityManager.flush()

        // Then
        val updated = orgRepository.findById(savedOrg.id!!).get()
        assertEquals("Updated Org", updated.name)
        assertEquals("456 Updated St", updated.address)
        assertEquals("555-5555", updated.phone)
        assertEquals("Updated City", updated.city)
        assertEquals("Updated Country", updated.country)
        assertEquals("Updated Notes", updated.notes)
        assertEquals(testOrgType.name, updated.orgTypeName)
    }
}