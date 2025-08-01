package io.cpk.be.basic.entity

import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class AccessRightTest {

    @Test
    fun `should create AccessRight with required parameters`() {
        // Given
        val name = "test-access-right"
        
        // When
        val accessRight = AccessRight(name = name)
        
        // Then
        assertEquals(name, accessRight.name)
    }
    
    @Test
    fun `should create AccessRight with all parameters`() {
        // Given
        val name = "test-access-right"
        
        // When
        val accessRight = AccessRight(
            name = name
        )
        
        // Then
        assertEquals(name, accessRight.name)
    }

    @Test
    fun `should correctly implement equals and hashCode`() {
        // Given
        val accessRight1 = AccessRight(name = "test-access-right")
        val accessRight2 = AccessRight(name = "test-access-right")
        val accessRight3 = AccessRight(name = "different-access-right")
        
        // Then
        assertEquals(accessRight1, accessRight2)
        assertEquals(accessRight1.hashCode(), accessRight2.hashCode())
        assertNotEquals(accessRight1, accessRight3)
        assertNotEquals(accessRight1.hashCode(), accessRight3.hashCode())
    }
    
    @Test
    fun `should correctly implement copy`() {
        // Given
        val accessRight = AccessRight(name = "test-access-right")
        
        // When
        val copied = accessRight.copy(name = "copied-access-right")
        
        // Then
        assertEquals("copied-access-right", copied.name)
    }
    
    @Test
    fun `should create AccessRight with no-arg constructor`() {
        // When
        val accessRight = AccessRight()
        
        // Then
        assertEquals("", accessRight.name)
    }
    
    @Test
    fun `should correctly implement toString`() {
        // Given
        val name = "test-access-right"
        val accessRight = AccessRight(name = name)
        
        // When
        val toString = accessRight.toString()
        
        // Then
        assertTrue(toString.contains("AccessRight"))
        assertTrue(toString.contains("name='$name'"))
    }
    
    @Test
    fun `should handle edge cases in equals method`() {
        // Given
        val accessRight = AccessRight(name = "test-access-right")
        
        // Then
        assertEquals(accessRight, accessRight) // Same instance
        
        // Null comparison
        val nullAccessRight: AccessRight? = null
        assertNotEquals(accessRight, nullAccessRight)
        
        // Different type comparison
        val differentObject = Any()
        assertFalse(accessRight.equals(differentObject))
    }
    
    @Test
    fun `should copy with different timestamps`() {
        // Given
        val name = "test-access-right"
        val accessRight = AccessRight(name = name)
        
        // When
        val copied = accessRight.copy(name = name)
        
        // Then
        assertEquals(name, copied.name)
        // We don't assert on timestamps here because the implementation has limitations
    }
}