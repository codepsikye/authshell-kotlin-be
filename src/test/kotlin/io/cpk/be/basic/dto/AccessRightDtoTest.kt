package io.cpk.be.basic.dto

import jakarta.validation.Validation
import jakarta.validation.Validator
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class AccessRightDtoTest {
    
    private lateinit var validator: Validator
    
    @BeforeEach
    fun setUp() {
        val factory = Validation.buildDefaultValidatorFactory()
        validator = factory.validator
    }

    @Test
    fun `should create AccessRightDto with required parameters`() {
        // Given
        val name = "test-access-right"
        
        // When
        val accessRightDto = AccessRightDto(name = name)
        
        // Then
        assertEquals(name, accessRightDto.name)
    }
    
    @Test
    fun `should correctly implement equals and hashCode`() {
        // Given
        val accessRightDto1 = AccessRightDto(name = "test-access-right")
        val accessRightDto2 = AccessRightDto(name = "test-access-right")
        val accessRightDto3 = AccessRightDto(name = "different-access-right")
        
        // Then
        assertEquals(accessRightDto1, accessRightDto2)
        assertEquals(accessRightDto1.hashCode(), accessRightDto2.hashCode())
        assertNotEquals(accessRightDto1, accessRightDto3)
        assertNotEquals(accessRightDto1.hashCode(), accessRightDto3.hashCode())
    }
    
    @Test
    fun `should correctly implement copy`() {
        // Given
        val accessRightDto = AccessRightDto(name = "test-access-right")
        
        // When
        val copied = accessRightDto.copy(name = "copied-access-right")
        
        // Then
        assertEquals("copied-access-right", copied.name)
    }
    
    @Test
    fun `should validate name is not empty`() {
        // Given
        val accessRightDto = AccessRightDto(name = "")
        
        // When
        val violations = validator.validate(accessRightDto)
        
        // Then
        assertEquals(1, violations.size)
        assertEquals("Name cannot be empty", violations.first().message)
    }
    
    @Test
    fun `should pass validation with valid name`() {
        // Given
        val accessRightDto = AccessRightDto(name = "test-access-right")
        
        // When
        val violations = validator.validate(accessRightDto)
        
        // Then
        assertEquals(0, violations.size)
    }
}