package io.cpk.be.basic.entity

import io.cpk.be.basic.dto.OrgConfig
import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import kotlin.test.*

class OrgTypeTest {

    @Test
    fun `should create OrgType with required parameters`() {
        // Given
        val name = "test-org-type"
        val config = OrgConfig()
        
        // When
        val orgType = OrgType(
            name = name,
            orgConfigs = config
        )
        
        // Then
        assertEquals(name, orgType.name)
        assertEquals(emptyList<String>(), orgType.accessRight)
        assertEquals(config.toMap(), orgType.orgConfigs.toMap())
        assertNotNull(orgType.createdAt)
        assertNotNull(orgType.updatedAt)
    }
    
    @Test
    fun `should create OrgType with all parameters`() {
        // Given
        val name = "test-org-type"
        val accessRight = listOf("read", "write", "delete")
        val config = OrgConfig(mapOf("key1" to "value1", "key2" to "value2"))
        
        // When
        val orgType = OrgType(
            name = name,
            accessRight = accessRight,
            orgConfigs = config
        )
        
        // Then
        assertEquals(name, orgType.name)
        assertEquals(accessRight, orgType.accessRight)
        assertEquals(config.toMap(), orgType.orgConfigs.toMap())
        assertNotNull(orgType.createdAt)
        assertNotNull(orgType.updatedAt)
    }
    
    @Test
    fun `should use default values for optional parameters if not provided`() {
        // Given
        val name = "test-org-type"
        val config = OrgConfig()
        val beforeCreation = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS)
        
        // When
        val orgType = OrgType(
            name = name,
            orgConfigs = config
        )
        
        // Then
        val afterCreation = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS)
        
        assertEquals(emptyList<String>(), orgType.accessRight)
        assertEquals(config.toMap(), orgType.orgConfigs.toMap())
        
        // Check that timestamps are between beforeCreation and afterCreation
        assert(orgType.createdAt.truncatedTo(ChronoUnit.SECONDS) >= beforeCreation)
        assert(orgType.createdAt.truncatedTo(ChronoUnit.SECONDS) <= afterCreation)
        assert(orgType.updatedAt.truncatedTo(ChronoUnit.SECONDS) >= beforeCreation)
        assert(orgType.updatedAt.truncatedTo(ChronoUnit.SECONDS) <= afterCreation)
    }
    
    @Test
    fun `should correctly implement equals and hashCode`() {
        // Given
        val config1 = OrgConfig(mapOf("key" to "value"))
        val config2 = OrgConfig(mapOf("key" to "value"))
        val config3 = OrgConfig(mapOf("key" to "value"))
        
        val orgType1 = OrgType(
            name = "test-org-type",
            accessRight = listOf("read", "write"),
            orgConfigs = config1
        )
        val orgType2 = OrgType(
            name = "test-org-type",
            accessRight = listOf("read", "write"),
            orgConfigs = config2
        )
        val orgType3 = OrgType(
            name = "different-org-type",
            accessRight = listOf("read", "write"),
            orgConfigs = config3
        )
        
        // Then
        assertEquals(orgType1, orgType2)
        assertEquals(orgType1.hashCode(), orgType2.hashCode())
        assertNotEquals(orgType1, orgType3)
        assertNotEquals(orgType1.hashCode(), orgType3.hashCode())
    }
    
    @Test
    fun `should correctly implement copy`() {
        // Given
        val config1 = OrgConfig(mapOf("key1" to "value1"))
        val config2 = OrgConfig(mapOf("key1" to "value1", "key2" to "value2"))
        
        val orgType = OrgType(
            name = "test-org-type",
            accessRight = listOf("read"),
            orgConfigs = config1
        )
        
        // When
        val copied = orgType.copy(
            name = "new-org-type",
            accessRight = listOf("read", "write", "delete"),
            orgConfigs = config2
        )
        
        // Then
        assertEquals("new-org-type", copied.name)
        assertEquals(listOf("read", "write", "delete"), copied.accessRight)
        assertEquals(config2.toMap(), copied.orgConfigs.toMap())
        assertNotNull(copied.createdAt)
        assertNotNull(copied.updatedAt)
    }
    
    @Test
    fun `should create OrgType with no-arg constructor`() {
        // When
        val orgType = OrgType()
        
        // Then
        assertEquals("", orgType.name)
        assertEquals(emptyList<String>(), orgType.accessRight)
        assertTrue(orgType.orgConfigs is OrgConfig)
        assertNotNull(orgType.createdAt)
        assertNotNull(orgType.updatedAt)
    }
    
    @Test
    fun `should correctly implement toString`() {
        // Given
        val name = "test-org-type"
        val accessRight = listOf("read", "write")
        val config = OrgConfig(mapOf("key1" to "value1"))
        
        val orgType = OrgType(
            name = name,
            accessRight = accessRight,
            orgConfigs = config
        )
        
        // When
        val toString = orgType.toString()
        
        // Then
        assertTrue(toString.contains("OrgType"))
        assertTrue(toString.contains("name='$name'"))
        assertTrue(toString.contains("accessRight=$accessRight"))
        assertTrue(toString.contains("orgConfigs=$config"))
    }
    
    @Test
    fun `should handle edge cases in equals method`() {
        // Given
        val config = OrgConfig()
        val orgType = OrgType(name = "test-org-type", orgConfigs = config)
        
        // Then
        assertEquals(orgType, orgType) // Same instance
        
        // Null comparison
        val nullOrgType: OrgType? = null
        assertNotEquals(orgType, nullOrgType)
        
        // Different type comparison
        val differentObject = Any()
        assertFalse(orgType.equals(differentObject))
    }
    
    @Test
    fun `should test all branches in equals method`() {
        // Given
        val name = "test-org-type"
        val accessRight1 = listOf("read", "write")
        val accessRight2 = listOf("read")
        val config1 = OrgConfig(mapOf("key1" to "value1"))
        val config2 = OrgConfig(mapOf("key2" to "value2"))
        
        val orgType1 = OrgType(name = name, accessRight = accessRight1, orgConfigs = config1)
        val orgType2 = OrgType(name = name, accessRight = accessRight2, orgConfigs = config1)
        val orgType3 = OrgType(name = name, accessRight = accessRight1, orgConfigs = config2)
        
        // Then
        // Different accessRight
        assertNotEquals(orgType1, orgType2)
        
        // Different orgConfigs
        assertNotEquals(orgType1, orgType3)
    }
}