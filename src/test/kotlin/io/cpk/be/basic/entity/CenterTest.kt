package io.cpk.be.basic.entity

import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class CenterTest {

    @Test
    fun `should create Center with required parameters`() {
        // Given
        val name = "Test Center"
        val testOrg = Org(id = 1, name = "Test Org", orgTypeName = "test-type")
        
        // When
        val center = Center(
            name = name,
            org = testOrg
        )
        
        // Then
        assertNull(center.id)
        assertEquals(name, center.name)
        assertNull(center.address)
        assertNull(center.phone)
        assertEquals(testOrg, center.org)
        assertNotNull(center.createdAt)
        assertNotNull(center.updatedAt)
    }
    
    @Test
    fun `should create Center with all parameters`() {
        // Given
        val id = 1
        val name = "Test Center"
        val address = "123 Test St"
        val phone = "555-1234"
        val org = Org(
            id = 1,
            name = "Test Org",
            orgTypeName = "test-type"
        )
        
        // When
        val center = Center(
            id = id,
            name = name,
            address = address,
            phone = phone,
            org = org
        )
        
        // Then
        assertEquals(id, center.id)
        assertEquals(name, center.name)
        assertEquals(address, center.address)
        assertEquals(phone, center.phone)
        assertNotNull(center.createdAt)
        assertNotNull(center.updatedAt)
        assertEquals(org, center.org)
    }
    
    @Test
    fun `should use default values for optional parameters if not provided`() {
        // Given
        val name = "Test Center"
        val testOrg = Org(id = 1, name = "Test Org", orgTypeName = "test-type")
        val beforeCreation = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS)
        
        // When
        val center = Center(
            name = name,
            org = testOrg
        )
        
        // Then
        val afterCreation = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS)
        
        assertNull(center.id)
        assertNull(center.address)
        assertNull(center.phone)
        assertEquals(testOrg, center.org)
        
        // Check that timestamps are between beforeCreation and afterCreation
        assert(center.createdAt.truncatedTo(ChronoUnit.SECONDS) >= beforeCreation)
        assert(center.createdAt.truncatedTo(ChronoUnit.SECONDS) <= afterCreation)
        assert(center.updatedAt.truncatedTo(ChronoUnit.SECONDS) >= beforeCreation)
        assert(center.updatedAt.truncatedTo(ChronoUnit.SECONDS) <= afterCreation)
    }
    
    @Test
    fun `should correctly implement equals and hashCode`() {
        // Given
        val testOrg = Org(id = 1, name = "Test Org", orgTypeName = "test-type")
        val center1 = Center(
            id = 1,
            name = "Test Center",
            org = testOrg
        )
        val center2 = Center(
            id = 1,
            name = "Test Center",
            org = testOrg
        )
        val center3 = Center(
            id = 2,
            name = "Test Center",
            org = testOrg
        )
        
        // Then
        assertEquals(center1, center2)
        assertEquals(center1.hashCode(), center2.hashCode())
        assertNotEquals(center1, center3)
        assertNotEquals(center1.hashCode(), center3.hashCode())
    }
    
    @Test
    fun `should correctly implement copy`() {
        // Given
        val testOrg = Org(id = 1, name = "Test Org", orgTypeName = "test-type")
        val center = Center(
            id = 1,
            name = "Test Center",
            org = testOrg
        )
        
        // When
        val copied = center.copy(
            name = "New Center",
            address = "456 New St",
            phone = "555-5678"
        )
        
        // Then
        assertEquals(1, copied.id)
        assertEquals("New Center", copied.name)
        assertEquals("456 New St", copied.address)
        assertEquals("555-5678", copied.phone)
        assertEquals(testOrg, copied.org)
        assertNotNull(copied.createdAt)
        assertNotNull(copied.updatedAt)
    }
}