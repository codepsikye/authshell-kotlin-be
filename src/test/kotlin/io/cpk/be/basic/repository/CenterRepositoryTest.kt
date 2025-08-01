package io.cpk.be.basic.repository

import io.cpk.be.basic.entity.Center
import io.cpk.be.basic.entity.Org
import io.cpk.be.basic.entity.OrgType
import io.cpk.be.config.TestConfig
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager
import org.springframework.context.annotation.Import
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@DataJpaTest
@Import(TestConfig::class)
class CenterRepositoryTest {

    @Autowired private lateinit var entityManager: TestEntityManager

    @Autowired private lateinit var centerRepository: CenterRepository
    
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
    fun `should save and find center`() {
        // Given
        val center = Center(
            name = "Test Center",
            address = "123 Test St",
            phone = "555-1234",
            orgId = testOrg.id!!
        )

        // When
        val savedCenter = entityManager.persistAndFlush(center)
        val found = centerRepository.findById(savedCenter.id!!)

        // Then
        assertTrue(found.isPresent)
        val foundCenter = found.get()
        assertEquals(savedCenter.id, foundCenter.id)
        assertEquals("Test Center", foundCenter.name)
        assertEquals("123 Test St", foundCenter.address)
        assertEquals("555-1234", foundCenter.phone)
        assertEquals(testOrg.id, foundCenter.orgId)
    }

    @Test
    fun `should find all centers`() {
        // Given
        val center1 = Center(
            name = "Center One",
            address = "123 First St",
            phone = "555-1111",
            orgId = testOrg.id!!
        )
        val center2 = Center(
            name = "Center Two",
            address = "456 Second St",
            phone = "555-2222",
            orgId = testOrg.id!!
        )

        entityManager.persistAndFlush(center1)
        entityManager.persistAndFlush(center2)

        // When
        val centers = centerRepository.findAll()

        // Then
        assertTrue(centers.size >= 2)
        assertTrue(centers.any { it.name == "Center One" })
        assertTrue(centers.any { it.name == "Center Two" })
    }

    @Test
    fun `should delete center`() {
        // Given
        val center = Center(
            name = "Center to Delete",
            address = "123 Delete St",
            phone = "555-3333",
            orgId = testOrg.id!!
        )

        val savedCenter = entityManager.persistAndFlush(center)
        assertTrue(centerRepository.findById(savedCenter.id!!).isPresent)

        // When
        centerRepository.deleteById(savedCenter.id!!)
        entityManager.flush()

        // Then
        assertTrue(centerRepository.findById(savedCenter.id!!).isEmpty)
    }

    @Test
    fun `should update center`() {
        // Given
        val center = Center(
            name = "Center to Update",
            address = "123 Update St",
            phone = "555-4444",
            orgId = testOrg.id!!
        )

        val savedCenter = entityManager.persistAndFlush(center)

        // When
        val foundCenter = centerRepository.findById(savedCenter.id!!).get()
        val updatedCenter = foundCenter.copy(
            name = "Updated Center",
            address = "456 Updated St",
            phone = "555-5555"
        )
        centerRepository.save(updatedCenter)
        entityManager.flush()

        // Then
        val updated = centerRepository.findById(savedCenter.id!!).get()
        assertEquals("Updated Center", updated.name)
        assertEquals("456 Updated St", updated.address)
        assertEquals("555-5555", updated.phone)
        assertEquals(testOrg.id, updated.orgId)
    }
}