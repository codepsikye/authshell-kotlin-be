package io.cpk.be.basic.entity

import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import kotlin.test.*

class OrgTest {

    @Test
    fun `should create Org with required parameters`() {
        // Given
        val name = "Test Organization"
        val orgTypeName = "test-org-type"
        
        // When
        val org = Org(
            name = name,
            orgTypeName = orgTypeName
        )
        
        // Then
        assertNull(org.id)
        assertEquals(name, org.name)
        assertNull(org.address)
        assertNull(org.phone)
        assertNull(org.city)
        assertNull(org.country)
        assertNull(org.notes)
        assertEquals(orgTypeName, org.orgTypeName)
        assertNotNull(org.createdAt)
        assertNotNull(org.updatedAt)
        assertNull(org.orgType)
    }
    
    @Test
    fun `should create Org with all parameters`() {
        // Given
        val id = 1
        val name = "Test Organization"
        val address = "123 Org St"
        val phone = "555-1234"
        val city = "Test City"
        val country = "Test Country"
        val notes = "Test Notes"
        val orgTypeName = "test-org-type"
        val orgType = OrgType(
            name = orgTypeName,
            accessRight = listOf("read", "write"),
            orgConfigs = mapOf("key" to "value")
        )
        
        // When
        val org = Org(
            id = id,
            name = name,
            address = address,
            phone = phone,
            city = city,
            country = country,
            notes = notes,
            orgTypeName = orgTypeName,
            orgType = orgType
        )
        
        // Then
        assertEquals(id, org.id)
        assertEquals(name, org.name)
        assertEquals(address, org.address)
        assertEquals(phone, org.phone)
        assertEquals(city, org.city)
        assertEquals(country, org.country)
        assertEquals(notes, org.notes)
        assertEquals(orgTypeName, org.orgTypeName)
        assertNotNull(org.createdAt)
        assertNotNull(org.updatedAt)
        assertEquals(orgType, org.orgType)
    }
    
    @Test
    fun `should use default values for optional parameters if not provided`() {
        // Given
        val name = "Test Organization"
        val orgTypeName = "test-org-type"
        val beforeCreation = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS)
        
        // When
        val org = Org(
            name = name,
            orgTypeName = orgTypeName
        )
        
        // Then
        val afterCreation = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS)
        
        assertNull(org.id)
        assertNull(org.address)
        assertNull(org.phone)
        assertNull(org.city)
        assertNull(org.country)
        assertNull(org.notes)
        assertNull(org.orgType)
        
        // Check that timestamps are between beforeCreation and afterCreation
        assert(org.createdAt.truncatedTo(ChronoUnit.SECONDS) >= beforeCreation)
        assert(org.createdAt.truncatedTo(ChronoUnit.SECONDS) <= afterCreation)
        assert(org.updatedAt.truncatedTo(ChronoUnit.SECONDS) >= beforeCreation)
        assert(org.updatedAt.truncatedTo(ChronoUnit.SECONDS) <= afterCreation)
    }
    
    @Test
    fun `should correctly implement equals and hashCode`() {
        // Given
        val org1 = Org(
            id = 1,
            name = "Test Organization",
            orgTypeName = "test-org-type"
        )
        val org2 = Org(
            id = 1,
            name = "Test Organization",
            orgTypeName = "test-org-type"
        )
        val org3 = Org(
            id = 2,
            name = "Test Organization",
            orgTypeName = "test-org-type"
        )
        
        // Then
        assertEquals(org1, org2)
        assertEquals(org1.hashCode(), org2.hashCode())
        assertNotEquals(org1, org3)
        assertNotEquals(org1.hashCode(), org3.hashCode())
    }
    
    @Test
    fun `should correctly implement copy`() {
        // Given
        val org = Org(
            id = 1,
            name = "Test Organization",
            orgTypeName = "test-org-type"
        )
        
        // When
        val copied = org.copy(
            name = "New Organization",
            address = "456 New St",
            phone = "555-5678",
            city = "New City",
            country = "New Country",
            notes = "New Notes"
        )
        
        // Then
        assertEquals(1, copied.id)
        assertEquals("New Organization", copied.name)
        assertEquals("456 New St", copied.address)
        assertEquals("555-5678", copied.phone)
        assertEquals("New City", copied.city)
        assertEquals("New Country", copied.country)
        assertEquals("New Notes", copied.notes)
        assertEquals("test-org-type", copied.orgTypeName)
        assertNotNull(copied.createdAt)
        assertNotNull(copied.updatedAt)
    }
    
    @Test
    fun `should create Org with no-arg constructor`() {
        // When
        val org = Org()
        
        // Then
        assertNull(org.id)
        assertEquals("", org.name)
        assertNull(org.address)
        assertNull(org.phone)
        assertNull(org.city)
        assertNull(org.country)
        assertNull(org.notes)
        assertEquals("", org.orgTypeName)
        assertNull(org.orgType)
        assertNotNull(org.createdAt)
        assertNotNull(org.updatedAt)
    }
    
    @Test
    fun `should correctly implement toString`() {
        // Given
        val id = 1
        val name = "Test Organization"
        val address = "123 Org St"
        val phone = "555-1234"
        val city = "Test City"
        val country = "Test Country"
        val notes = "Test Notes"
        val orgTypeName = "test-org-type"
        
        val org = Org(
            id = id,
            name = name,
            address = address,
            phone = phone,
            city = city,
            country = country,
            notes = notes,
            orgTypeName = orgTypeName
        )
        
        // When
        val toString = org.toString()
        
        // Then
        assertTrue(toString.contains("Org"))
        assertTrue(toString.contains("id=$id"))
        assertTrue(toString.contains("name='$name'"))
        assertTrue(toString.contains("address=$address"))
        assertTrue(toString.contains("phone=$phone"))
        assertTrue(toString.contains("city=$city"))
        assertTrue(toString.contains("country=$country"))
        assertTrue(toString.contains("notes=$notes"))
        assertTrue(toString.contains("orgTypeName='$orgTypeName'"))
    }
    
    @Test
    fun `should handle edge cases in equals method`() {
        // Given
        val org = Org(name = "Test Organization", orgTypeName = "test-org-type")
        
        // Then
        assertEquals(org, org) // Same instance
        
        // Null comparison
        val nullOrg: Org? = null
        assertNotEquals(org, nullOrg)
        
        // Different type comparison
        val differentObject = Any()
        assertFalse(org.equals(differentObject))
    }
    
    @Test
    fun `should test all branches in equals method`() {
        // Given
        val baseOrg = Org(
            id = 1,
            name = "Test Organization",
            address = "123 Org St",
            phone = "555-1234",
            city = "Test City",
            country = "Test Country",
            notes = "Test Notes",
            orgTypeName = "test-org-type"
        )
        
        // Different id
        val orgDiffId = baseOrg.copy(id = 2)
        assertNotEquals(baseOrg, orgDiffId)
        
        // Different name
        val orgDiffName = baseOrg.copy(name = "Different Organization")
        assertNotEquals(baseOrg, orgDiffName)
        
        // Different address
        val orgDiffAddress = baseOrg.copy(address = "456 Different St")
        assertNotEquals(baseOrg, orgDiffAddress)
        
        // Different phone
        val orgDiffPhone = baseOrg.copy(phone = "555-5678")
        assertNotEquals(baseOrg, orgDiffPhone)
        
        // Different city
        val orgDiffCity = baseOrg.copy(city = "Different City")
        assertNotEquals(baseOrg, orgDiffCity)
        
        // Different country
        val orgDiffCountry = baseOrg.copy(country = "Different Country")
        assertNotEquals(baseOrg, orgDiffCountry)
        
        // Different notes
        val orgDiffNotes = baseOrg.copy(notes = "Different Notes")
        assertNotEquals(baseOrg, orgDiffNotes)
        
        // Different orgTypeName
        val orgDiffOrgTypeName = baseOrg.copy(orgTypeName = "different-org-type")
        assertNotEquals(baseOrg, orgDiffOrgTypeName)
    }
}