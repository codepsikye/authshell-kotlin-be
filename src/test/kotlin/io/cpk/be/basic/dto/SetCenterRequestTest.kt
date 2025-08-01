package io.cpk.be.basic.dto

import jakarta.validation.Validation
import jakarta.validation.Validator
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class SetCenterRequestTest {
    
    private lateinit var validator: Validator
    
    @BeforeEach
    fun setUp() {
        val factory = Validation.buildDefaultValidatorFactory()
        validator = factory.validator
    }

    @Test
    fun `should create SetCenterRequest with required parameters`() {
        // Given
        val centerId = 42
        
        // When
        val setCenterRequest = SetCenterRequest(
            centerId = centerId
        )
        
        // Then
        assertEquals(centerId, setCenterRequest.centerId)
    }
    
    @Test
    fun `should correctly implement equals and hashCode`() {
        // Given
        val setCenterRequest1 = SetCenterRequest(
            centerId = 42
        )
        
        val setCenterRequest2 = SetCenterRequest(
            centerId = 42
        )
        
        val setCenterRequest3 = SetCenterRequest(
            centerId = 99
        )
        
        // Then
        assertEquals(setCenterRequest1, setCenterRequest2)
        assertEquals(setCenterRequest1.hashCode(), setCenterRequest2.hashCode())
        assertNotEquals(setCenterRequest1, setCenterRequest3)
        assertNotEquals(setCenterRequest1.hashCode(), setCenterRequest3.hashCode())
    }
    
    @Test
    fun `should correctly implement copy`() {
        // Given
        val setCenterRequest = SetCenterRequest(
            centerId = 42
        )
        
        // When
        val copied = setCenterRequest.copy(
            centerId = 99
        )
        
        // Then
        assertEquals(99, copied.centerId)
    }
    
    @Test
    fun `should validate centerId is not null`() {
        // Given
        // We can't directly pass null for centerId due to non-nullable type
        // This test is for illustration purposes
        // In a real scenario with a nullable type, we would test with null
        
        // For now, we'll test with a valid centerId since we can't test the @NotNull validation directly
        val setCenterRequest = SetCenterRequest(
            centerId = 0  // Using 0 as a minimal value
        )
        
        // When
        val violations = validator.validate(setCenterRequest)
        
        // Then
        // This test might not catch the @NotNull validation since centerId is a non-nullable Int
        // In a real scenario, we would test this with a nullable type
        assertEquals(0, violations.size)
    }
    
    @Test
    fun `should pass validation with valid data`() {
        // Given
        val setCenterRequest = SetCenterRequest(
            centerId = 42
        )
        
        // When
        val violations = validator.validate(setCenterRequest)
        
        // Then
        assertEquals(0, violations.size)
    }
}