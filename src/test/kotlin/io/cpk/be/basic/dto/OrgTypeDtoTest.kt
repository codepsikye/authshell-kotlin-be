package io.cpk.be.basic.dto

import jakarta.validation.Validation
import jakarta.validation.Validator
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class OrgTypeDtoTest {
    
    private lateinit var validator: Validator
    
    @BeforeEach
    fun setUp() {
        val factory = Validation.buildDefaultValidatorFactory()
        validator = factory.validator
    }

    @Test
    fun `should create OrgTypeDto with required parameters`() {
        // Given
        val name = "Corporation"
        val config = OrgConfig()
        
        // When
        val orgTypeDto = OrgTypeDto(
            name = name,
            orgConfigs = config
        )
        
        // Then
        assertEquals(name, orgTypeDto.name)
        assertEquals(emptyList<String>(), orgTypeDto.accessRight)
        assertEquals(config.toMap(), orgTypeDto.orgConfigs.toMap())
    }
    
    @Test
    fun `should create OrgTypeDto with all parameters`() {
        // Given
        val name = "Corporation"
        val accessRight = listOf("READ", "WRITE")
        val config = OrgConfig(mapOf("key1" to "value1", "key2" to 42))
        
        // When
        val orgTypeDto = OrgTypeDto(
            name = name,
            accessRight = accessRight,
            orgConfigs = config
        )
        
        // Then
        assertEquals(name, orgTypeDto.name)
        assertEquals(accessRight, orgTypeDto.accessRight)
        assertEquals(config.toMap(), orgTypeDto.orgConfigs.toMap())
    }
    
    @Test
    fun `should correctly implement equals and hashCode`() {
        // Given
        val config1 = OrgConfig()
        val config2 = OrgConfig()
        val config3 = OrgConfig()
        
        val orgTypeDto1 = OrgTypeDto(
            name = "Corporation",
            accessRight = listOf("READ", "WRITE"),
            orgConfigs = config1
        )
        
        val orgTypeDto2 = OrgTypeDto(
            name = "Corporation",
            accessRight = listOf("READ", "WRITE"),
            orgConfigs = config2
        )
        
        val orgTypeDto3 = OrgTypeDto(
            name = "Non-Profit",
            accessRight = listOf("READ", "WRITE"),
            orgConfigs = config3
        )
        
        // Then
        assertEquals(orgTypeDto1, orgTypeDto2)
        assertEquals(orgTypeDto1.hashCode(), orgTypeDto2.hashCode())
        assertNotEquals(orgTypeDto1, orgTypeDto3)
        assertNotEquals(orgTypeDto1.hashCode(), orgTypeDto3.hashCode())
    }
    
    @Test
    fun `should correctly implement copy`() {
        // Given
        val config = OrgConfig()
        val orgTypeDto = OrgTypeDto(
            name = "Corporation",
            accessRight = listOf("READ", "WRITE"),
            orgConfigs = config
        )
        
        // When
        val copied = orgTypeDto.copy(
            name = "Updated Corporation",
            accessRight = listOf("READ", "WRITE", "DELETE")
        )
        
        // Then
        assertEquals("Updated Corporation", copied.name)
        assertEquals(listOf("READ", "WRITE", "DELETE"), copied.accessRight)
        assertEquals(config.toMap(), copied.orgConfigs.toMap())
    }
    
    @Test
    fun `should validate name is not empty`() {
        // Given
        val config = OrgConfig()
        val orgTypeDto = OrgTypeDto(
            name = "",
            orgConfigs = config
        )
        
        // When
        val violations = validator.validate(orgTypeDto)
        
        // Then
        assertEquals(1, violations.size)
        assertEquals("Name cannot be empty", violations.first().message)
    }
    
    @Test
    fun `should pass validation with valid data`() {
        // Given
        val config = OrgConfig()
        val orgTypeDto = OrgTypeDto(
            name = "Corporation",
            orgConfigs = config
        )
        
        // When
        val violations = validator.validate(orgTypeDto)
        
        // Then
        assertEquals(0, violations.size)
    }
    
    @Test
    fun `should handle complex orgConfigs`() {
        // Given
        val name = "Corporation"
        val config = OrgConfig(mapOf(
            "stringValue" to "text",
            "intValue" to 42,
            "boolValue" to true,
            "listValue" to listOf(1, 2, 3),
            "mapValue" to mapOf("nested" to "value")
        ))
        
        // When
        val orgTypeDto = OrgTypeDto(
            name = name,
            orgConfigs = config
        )
        
        // Then
        assertEquals(name, orgTypeDto.name)
        assertEquals(config.toMap(), orgTypeDto.orgConfigs.toMap())
        // We can't directly access properties by key since OrgConfig doesn't support this
        // Instead, we verify the toMap() representation
        val configMap = orgTypeDto.orgConfigs.toMap()
        assertEquals("text", configMap["stringValue"])
        assertEquals(42, configMap["intValue"])
        assertEquals(true, configMap["boolValue"])
        assertEquals(listOf(1, 2, 3), configMap["listValue"])
        assertEquals(mapOf("nested" to "value"), configMap["mapValue"])
    }
}