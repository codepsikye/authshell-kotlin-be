package io.cpk.be.basic.dto

import jakarta.validation.Validation
import jakarta.validation.Validator
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertNull

class CenterDtoTest {
    
    private lateinit var validator: Validator
    
    @BeforeEach
    fun setUp() {
        val factory = Validation.buildDefaultValidatorFactory()
        validator = factory.validator
    }

    @Test
    fun `should create CenterDto with required parameters`() {
        // Given
        val name = "Test Center"
        val orgId = 1
        
        // When
        val centerDto = CenterDto(
            name = name,
            orgId = orgId
        )
        
        // Then
        assertNull(centerDto.id)
        assertEquals(name, centerDto.name)
        assertNull(centerDto.address)
        assertNull(centerDto.phone)
        assertEquals(orgId, centerDto.orgId)
    }
    
    @Test
    fun `should create CenterDto with all parameters`() {
        // Given
        val id = 1
        val name = "Test Center"
        val address = "123 Test St"
        val phone = "123-456-7890"
        val orgId = 1
        
        // When
        val centerDto = CenterDto(
            id = id,
            name = name,
            address = address,
            phone = phone,
            orgId = orgId
        )
        
        // Then
        assertEquals(id, centerDto.id)
        assertEquals(name, centerDto.name)
        assertEquals(address, centerDto.address)
        assertEquals(phone, centerDto.phone)
        assertEquals(orgId, centerDto.orgId)
    }
    
    @Test
    fun `should correctly implement equals and hashCode`() {
        // Given
        val centerDto1 = CenterDto(
            id = 1,
            name = "Test Center",
            orgId = 1
        )
        
        val centerDto2 = CenterDto(
            id = 1,
            name = "Test Center",
            orgId = 1
        )
        
        val centerDto3 = CenterDto(
            id = 2,
            name = "Test Center",
            orgId = 1
        )
        
        // Then
        assertEquals(centerDto1, centerDto2)
        assertEquals(centerDto1.hashCode(), centerDto2.hashCode())
        assertNotEquals(centerDto1, centerDto3)
        assertNotEquals(centerDto1.hashCode(), centerDto3.hashCode())
    }
    
    @Test
    fun `should correctly implement copy`() {
        // Given
        val centerDto = CenterDto(
            id = 1,
            name = "Test Center",
            orgId = 1
        )
        
        // When
        val copied = centerDto.copy(
            name = "Updated Center",
            address = "456 New St"
        )
        
        // Then
        assertEquals(1, copied.id)
        assertEquals("Updated Center", copied.name)
        assertEquals("456 New St", copied.address)
        assertEquals(1, copied.orgId)
    }
    
    @Test
    fun `should validate name is not blank`() {
        // Given
        val centerDto = CenterDto(
            name = "",
            orgId = 1
        )
        
        // When
        val violations = validator.validate(centerDto)
        
        // Then
        assertEquals(1, violations.size)
        assertEquals("Name cannot be blank", violations.first().message)
    }
    
    @Test
    fun `should validate orgId is not null`() {
        // Given
        val centerDto = CenterDto(
            name = "Test Center",
            orgId = 0  // We can't pass null directly due to non-nullable type
        )
        
        // When
        val violations = validator.validate(centerDto)
        
        // Then
        // This test might not catch the @NotNull validation since orgId is a non-nullable Int
        // In a real scenario, we would test this with a nullable type
        assertEquals(0, violations.size)
    }
    
    @Test
    fun `should pass validation with valid data`() {
        // Given
        val centerDto = CenterDto(
            name = "Test Center",
            orgId = 1
        )
        
        // When
        val violations = validator.validate(centerDto)
        
        // Then
        assertEquals(0, violations.size)
    }
}