package io.cpk.be.basic.entity

import io.cpk.be.basic.repository.CenterRepository
import io.cpk.be.basic.repository.OrgRepository
import io.cpk.be.basic.repository.OrgTypeRepository
import io.cpk.be.config.JpaAuditingConfig
import io.cpk.be.config.TestConfig
import io.cpk.be.security.CustomUserDetails
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager
import org.springframework.context.annotation.Import
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.test.context.ActiveProfiles
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import kotlin.test.*

/**
 * Integration tests for BaseAuditable entity functionality.
 * Tests that entities extending BaseAuditable have proper audit fields and basic functionality.
 * 
 * Note: Due to the immutable nature of the entity design (val properties with constructor initialization),
 * full JPA auditing behavior (automatic timestamp updates) is limited. These tests focus on verifying
 * that audit fields are present and properly initialized.
 */
@DataJpaTest
@Import(JpaAuditingConfig::class, TestConfig::class)
@ActiveProfiles("test")
class BaseAuditableIntegrationTest {

    @Autowired
    private lateinit var entityManager: TestEntityManager

    @Autowired
    private lateinit var centerRepository: CenterRepository

    @Autowired
    private lateinit var orgRepository: OrgRepository

    @Autowired
    private lateinit var orgTypeRepository: OrgTypeRepository

    private lateinit var testOrgType: OrgType
    private lateinit var testOrg: Org

    @BeforeEach
    fun setUp() {
        // Clear the security context
        SecurityContextHolder.clearContext()

        // Create test data
        testOrgType = OrgType(
            name = "test-org-type",
            accessRight = listOf("read", "write"),
            orgConfigs = mapOf("key" to "value")
        )
        entityManager.persistAndFlush(testOrgType)

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
    fun `should automatically set createdAt and updatedAt on entity creation`() {
        // Given
        val beforeCreation = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS)

        val center = Center(
            name = "Test Center",
            address = "123 Test St",
            phone = "555-1234",
            orgId = testOrg.id!!
        )

        // When
        val savedCenter = entityManager.persistAndFlush(center)
        val afterCreation = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS)
        
        // Get truncated timestamps for comparison
        val createdAtTruncated = savedCenter.createdAt.truncatedTo(ChronoUnit.SECONDS)
        val updatedAtTruncated = savedCenter.updatedAt.truncatedTo(ChronoUnit.SECONDS)

        // Verify timestamps are within expected range
        assertTrue(createdAtTruncated >= beforeCreation, 
            "createdAt ($createdAtTruncated) should be after or equal to beforeCreation ($beforeCreation)")
        assertTrue(createdAtTruncated <= afterCreation, 
            "createdAt ($createdAtTruncated) should be before or equal to afterCreation ($afterCreation)")
        assertTrue(updatedAtTruncated >= beforeCreation, 
            "updatedAt ($updatedAtTruncated) should be after or equal to beforeCreation ($beforeCreation)")
        assertTrue(updatedAtTruncated <= afterCreation, 
            "updatedAt ($updatedAtTruncated) should be before or equal to afterCreation ($afterCreation)")

        // Both timestamps should be very close to each other on creation
        assertEquals(
            createdAtTruncated,
            updatedAtTruncated,
            "createdAt and updatedAt should be equal on entity creation"
        )
    }

    @Test
    fun `should set createdBy and updatedBy to system when no user is authenticated`() {
        // Given
        val center = Center(
            name = "Test Center",
            address = "123 Test St",
            phone = "555-1234",
            orgId = testOrg.id!!
        )

        // When
        val savedCenter = entityManager.persistAndFlush(center)

        // Then
        assertEquals("system", savedCenter.createdBy)
        assertEquals("system", savedCenter.updatedBy)
    }

    @Test
    fun `should set createdBy and updatedBy to system in test environment`() {
        // Given
        val center = Center(
            name = "Test Center",
            address = "123 Test St",
            phone = "555-1234",
            orgId = testOrg.id!!
        )

        // When
        val savedCenter = entityManager.persistAndFlush(center)

        // Then
        // In test environment, JPA auditing falls back to "system" as the default user
        // This verifies that the auditing configuration is working
        assertEquals("system", savedCenter.createdBy)
        assertEquals("system", savedCenter.updatedBy)
    }

    @Test
    fun `should have audit fields properly initialized on entity creation`() {
        // Given
        val beforeCreation = LocalDateTime.now().minusSeconds(1)
        
        val center = Center(
            name = "Test Center",
            address = "123 Test St",
            phone = "555-1234",
            orgId = testOrg.id!!
        )

        // When
        val savedCenter = entityManager.persistAndFlush(center)
        val afterCreation = LocalDateTime.now().plusSeconds(1)

        // Then - verify all audit fields are properly initialized
        assertNotNull(savedCenter.createdAt)
        assertNotNull(savedCenter.updatedAt)
        assertNotNull(savedCenter.createdBy)
        assertNotNull(savedCenter.updatedBy)
        
        // Verify timestamps are reasonable
        assertTrue(savedCenter.createdAt.isAfter(beforeCreation))
        assertTrue(savedCenter.createdAt.isBefore(afterCreation))
        assertTrue(savedCenter.updatedAt.isAfter(beforeCreation))
        assertTrue(savedCenter.updatedAt.isBefore(afterCreation))
        
        // Verify default user values
        assertEquals("system", savedCenter.createdBy)
        assertEquals("system", savedCenter.updatedBy)
    }

    @Test
    fun `should verify audit field annotations are properly configured`() {
        // Given
        val center = Center(
            name = "Test Center",
            address = "123 Test St",
            phone = "555-1234",
            orgId = testOrg.id!!
        )

        // When
        val savedCenter = entityManager.persistAndFlush(center)

        // Then - verify the entity has all required audit fields
        // This test ensures that the BaseAuditable inheritance is working correctly
        assertNotNull(savedCenter.createdAt, "createdAt should not be null")
        assertNotNull(savedCenter.updatedAt, "updatedAt should not be null")
        assertNotNull(savedCenter.createdBy, "createdBy should not be null")
        assertNotNull(savedCenter.updatedBy, "updatedBy should not be null")
        
        // Verify the audit fields have reasonable values
        assertTrue(savedCenter.createdBy.isNotEmpty(), "createdBy should not be empty")
        assertTrue(savedCenter.updatedBy.isNotEmpty(), "updatedBy should not be empty")
    }

    @Test
    fun `should handle CRUD operations with audit fields present`() {
        // CREATE
        val center = Center(
            name = "CRUD Test Center",
            address = "123 CRUD St",
            phone = "555-CRUD",
            orgId = testOrg.id!!
        )
        val createdCenter = entityManager.persistAndFlush(center)
        
        // Verify initial audit fields are set
        assertNotNull(createdCenter.createdAt)
        assertNotNull(createdCenter.updatedAt)
        assertEquals("system", createdCenter.createdBy)
        assertEquals("system", createdCenter.updatedBy)

        // READ
        val readCenter = centerRepository.findById(createdCenter.id!!).get()
        assertEquals("CRUD Test Center", readCenter.name)
        assertNotNull(readCenter.createdAt)
        assertNotNull(readCenter.updatedAt)

        // UPDATE (create new entity with updated data)
        val updatedCenter = Center(
            id = readCenter.id,
            name = "Updated CRUD Center",
            address = readCenter.address,
            phone = readCenter.phone,
            orgId = readCenter.orgId,
            org = readCenter.org
        )
        val savedUpdatedCenter = centerRepository.save(updatedCenter)
        entityManager.flush()
        
        val refreshedCenter = centerRepository.findById(savedUpdatedCenter.id!!).get()
        assertEquals("Updated CRUD Center", refreshedCenter.name)
        assertNotNull(refreshedCenter.createdAt)
        assertNotNull(refreshedCenter.updatedAt)

        // DELETE (verify audit fields exist before deletion)
        val centerToDelete = centerRepository.findById(refreshedCenter.id!!).get()
        assertNotNull(centerToDelete.createdAt)
        assertNotNull(centerToDelete.updatedAt)
        assertNotNull(centerToDelete.createdBy)
        assertNotNull(centerToDelete.updatedBy)
        
        centerRepository.deleteById(centerToDelete.id!!)
        entityManager.flush()
        
        assertTrue(centerRepository.findById(centerToDelete.id!!).isEmpty)
    }

    @Test
    fun `should work with different entity types extending BaseAuditable`() {
        // Test with Org entity
        val org = Org(
            name = "Multi Test Org",
            orgTypeName = testOrgType.name
        )
        val savedOrg = entityManager.persistAndFlush(org)

        // Test with OrgType entity
        val orgType = OrgType(
            name = "multi-test-type",
            accessRight = listOf("test"),
            orgConfigs = mapOf("test" to "value")
        )
        val savedOrgType = entityManager.persistAndFlush(orgType)

        // Then - all entities should have proper audit fields
        listOf(savedOrg, savedOrgType).forEach { entity ->
            assertNotNull(entity.createdAt)
            assertNotNull(entity.updatedAt)
            assertEquals("system", entity.createdBy)
            assertEquals("system", entity.updatedBy)
        }
    }

    @Test
    fun `should verify timestamp fields are properly set on entity creation`() {
        // Given
        val beforeCreation = LocalDateTime.now().minusSeconds(1)
        
        val center = Center(
            name = "Timestamp Test Center",
            address = "123 Timestamp St",
            phone = "555-TIME",
            orgId = testOrg.id!!
        )

        // When
        val savedCenter = entityManager.persistAndFlush(center)
        val afterCreation = LocalDateTime.now().plusSeconds(1)

        // Then - verify timestamps are within expected range
        assertNotNull(savedCenter.createdAt)
        assertNotNull(savedCenter.updatedAt)
        
        assertTrue(savedCenter.createdAt.isAfter(beforeCreation))
        assertTrue(savedCenter.createdAt.isBefore(afterCreation))
        assertTrue(savedCenter.updatedAt.isAfter(beforeCreation))
        assertTrue(savedCenter.updatedAt.isBefore(afterCreation))
        
        // Both timestamps should be very close to each other on creation
        val timeDifference = ChronoUnit.MILLIS.between(savedCenter.createdAt, savedCenter.updatedAt)
        assertTrue(Math.abs(timeDifference) < 1000, "createdAt and updatedAt should be within 1 second of each other")
    }

    @Test
    fun `should validate BaseAuditable inheritance works across different entity types`() {
        // Test with multiple entity types to ensure BaseAuditable works consistently
        
        // Test with Org entity
        val org = Org(
            name = "Inheritance Test Org",
            orgTypeName = testOrgType.name
        )
        val savedOrg = entityManager.persistAndFlush(org)

        // Test with OrgType entity
        val orgType = OrgType(
            name = "inheritance-test-type",
            accessRight = listOf("test"),
            orgConfigs = mapOf("test" to "value")
        )
        val savedOrgType = entityManager.persistAndFlush(orgType)

        // Test with Center entity
        val center = Center(
            name = "Inheritance Test Center",
            address = "123 Inheritance St",
            phone = "555-INHERIT",
            orgId = testOrg.id!!
        )
        val savedCenter = entityManager.persistAndFlush(center)

        // Then - all entities should have proper audit fields
        listOf(savedOrg, savedOrgType, savedCenter).forEach { entity ->
            assertNotNull(entity.createdAt, "${entity::class.simpleName} should have createdAt")
            assertNotNull(entity.updatedAt, "${entity::class.simpleName} should have updatedAt")
            assertEquals("system", entity.createdBy, "${entity::class.simpleName} should have correct createdBy")
            assertEquals("system", entity.updatedBy, "${entity::class.simpleName} should have correct updatedBy")
        }
    }

    private fun setAuthenticatedUser(username: String) {
        val authorities = listOf(SimpleGrantedAuthority("ROLE_USER"))
        val userDetails = CustomUserDetails(username, "password", authorities, 1, 1, 1)
        val authentication = UsernamePasswordAuthenticationToken(userDetails, null, authorities)
        SecurityContextHolder.getContext().authentication = authentication
    }
}