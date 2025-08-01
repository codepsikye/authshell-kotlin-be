package io.cpk.be.basic.dto

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class UserPrefsTest {

    @Test
    fun `should create UserPrefs with empty properties`() {
        // When
        val userPrefs = UserPrefs()
        
        // Then
        assertTrue(userPrefs.toMap().isEmpty())
        assertEquals(emptyMap<String, Any>(), userPrefs.toMap())
    }
    
    @Test
    fun `should create UserPrefs from map`() {
        // Given
        val map = mapOf(
            "theme" to "dark",
            "notifications" to false,
            "customField" to "customValue"
        )
        
        // When
        val userPrefs = UserPrefs.fromMap(map)
        
        // Then
        assertEquals("dark", userPrefs.getProperty("theme"))
        assertEquals(false, userPrefs.getProperty("notifications"))
        assertEquals("customValue", userPrefs.getProperty("customField"))
        assertEquals(map, userPrefs.toMap())
    }
    
    @Test
    fun `should get property values`() {
        // Given
        val userPrefs = UserPrefs()
        userPrefs.setProperty("customField", "customValue")
        
        // When/Then
        assertEquals("customValue", userPrefs.getProperty("customField"))
        assertNull(userPrefs.getProperty("nonExistentField"))
    }
    
    @Test
    fun `should set property values`() {
        // Given
        val userPrefs = UserPrefs()
        
        // When
        userPrefs.setProperty("theme", "dark")
            .setProperty("notifications", false)
            .setProperty("customField", "customValue")
            .setProperty("numberField", 42)
            .setProperty("boolField", true)
        
        // Then
        assertEquals("dark", userPrefs.getProperty("theme"))
        assertEquals(false, userPrefs.getProperty("notifications"))
        assertEquals("customValue", userPrefs.getProperty("customField"))
        assertEquals(42, userPrefs.getProperty("numberField"))
        assertEquals(true, userPrefs.getProperty("boolField"))
    }
    
    @Test
    fun `should remove properties`() {
        // Given
        val userPrefs = UserPrefs()
        userPrefs.setProperty("field1", "value1")
            .setProperty("field2", "value2")
        
        // When
        userPrefs.removeProperty("field1")
        
        // Then
        assertNull(userPrefs.getProperty("field1"))
        assertEquals("value2", userPrefs.getProperty("field2"))
        
        // When - remove another property
        userPrefs.removeProperty("field2")
        
        // Then - all properties should be removed
        assertTrue(userPrefs.toMap().isEmpty())
    }
    
    @Test
    fun `should check if property exists`() {
        // Given
        val userPrefs = UserPrefs()
        userPrefs.setProperty("field1", "value")
        
        // When/Then
        assertTrue(userPrefs.hasProperty("field1"))
        assertFalse(userPrefs.hasProperty("nonExistentField"))
    }
    
    @Test
    fun `should handle complex property values`() {
        // Given
        val userPrefs = UserPrefs()
        val nestedMap = mapOf("key1" to "value1", "key2" to 42)
        val list = listOf("item1", "item2", "item3")
        
        // When
        userPrefs.setProperty("nestedMap", nestedMap)
            .setProperty("list", list)
        
        // Then
        assertEquals(nestedMap, userPrefs.getProperty("nestedMap"))
        assertEquals(list, userPrefs.getProperty("list"))
    }
    
    @Test
    fun `should convert to map and back`() {
        // Given
        val userPrefs = UserPrefs()
        userPrefs.setProperty("theme", "dark")
            .setProperty("customField", "value")
            .setProperty("numberField", 42)
        
        // When
        val map = userPrefs.toMap()
        val recreatedUserPrefs = UserPrefs.fromMap(map)
        
        // Then
        assertEquals("dark", recreatedUserPrefs.getProperty("theme"))
        assertEquals("value", recreatedUserPrefs.getProperty("customField"))
        assertEquals(42, recreatedUserPrefs.getProperty("numberField"))
        assertEquals(userPrefs.toMap(), recreatedUserPrefs.toMap())
    }
}