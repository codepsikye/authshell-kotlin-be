package io.cpk.be.basic.dto

import jakarta.validation.Validation
import jakarta.validation.Validator
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertNull

class RefreshTokenRequestTest {
    
    private lateinit var validator: Validator
    
    @BeforeEach
    fun setUp() {
        val factory = Validation.buildDefaultValidatorFactory()
        validator = factory.validator
    }

    @Test
    fun `should create RefreshTokenRequest with required parameters`() {
        // Given
        val refreshToken = "refresh-token-123"
        
        // When
        val refreshTokenRequest = RefreshTokenRequest(
            refreshToken = refreshToken
        )
        
        // Then
        assertEquals(refreshToken, refreshTokenRequest.refreshToken)
        assertNull(refreshTokenRequest.centerId)
    }
    
    @Test
    fun `should create RefreshTokenRequest with all parameters`() {
        // Given
        val refreshToken = "refresh-token-123"
        val centerId = 42
        
        // When
        val refreshTokenRequest = RefreshTokenRequest(
            refreshToken = refreshToken,
            centerId = centerId
        )
        
        // Then
        assertEquals(refreshToken, refreshTokenRequest.refreshToken)
        assertEquals(centerId, refreshTokenRequest.centerId)
    }
    
    @Test
    fun `should correctly implement equals and hashCode`() {
        // Given
        val refreshTokenRequest1 = RefreshTokenRequest(
            refreshToken = "refresh-token-123",
            centerId = 42
        )
        
        val refreshTokenRequest2 = RefreshTokenRequest(
            refreshToken = "refresh-token-123",
            centerId = 42
        )
        
        val refreshTokenRequest3 = RefreshTokenRequest(
            refreshToken = "different-token",
            centerId = 42
        )
        
        val refreshTokenRequest4 = RefreshTokenRequest(
            refreshToken = "refresh-token-123",
            centerId = 99
        )
        
        // Then
        assertEquals(refreshTokenRequest1, refreshTokenRequest2)
        assertEquals(refreshTokenRequest1.hashCode(), refreshTokenRequest2.hashCode())
        assertNotEquals(refreshTokenRequest1, refreshTokenRequest3)
        assertNotEquals(refreshTokenRequest1.hashCode(), refreshTokenRequest3.hashCode())
        assertNotEquals(refreshTokenRequest1, refreshTokenRequest4)
        assertNotEquals(refreshTokenRequest1.hashCode(), refreshTokenRequest4.hashCode())
    }
    
    @Test
    fun `should correctly implement copy`() {
        // Given
        val refreshTokenRequest = RefreshTokenRequest(
            refreshToken = "refresh-token-123",
            centerId = 42
        )
        
        // When
        val copied1 = refreshTokenRequest.copy(
            refreshToken = "new-token-456"
        )
        
        val copied2 = refreshTokenRequest.copy(
            centerId = 99
        )
        
        val copied3 = refreshTokenRequest.copy(
            refreshToken = "new-token-456",
            centerId = null
        )
        
        // Then
        assertEquals("new-token-456", copied1.refreshToken)
        assertEquals(42, copied1.centerId)
        
        assertEquals("refresh-token-123", copied2.refreshToken)
        assertEquals(99, copied2.centerId)
        
        assertEquals("new-token-456", copied3.refreshToken)
        assertNull(copied3.centerId)
    }
    
    @Test
    fun `should validate refreshToken is not blank`() {
        // Given
        val refreshTokenRequest = RefreshTokenRequest(
            refreshToken = ""
        )
        
        // When
        val violations = validator.validate(refreshTokenRequest)
        
        // Then
        assertEquals(1, violations.size)
        assertEquals("Refresh token cannot be blank", violations.first().message)
    }
    
    @Test
    fun `should pass validation with valid data and null centerId`() {
        // Given
        val refreshTokenRequest = RefreshTokenRequest(
            refreshToken = "refresh-token-123"
        )
        
        // When
        val violations = validator.validate(refreshTokenRequest)
        
        // Then
        assertEquals(0, violations.size)
    }
    
    @Test
    fun `should pass validation with valid data and non-null centerId`() {
        // Given
        val refreshTokenRequest = RefreshTokenRequest(
            refreshToken = "refresh-token-123",
            centerId = 42
        )
        
        // When
        val violations = validator.validate(refreshTokenRequest)
        
        // Then
        assertEquals(0, violations.size)
    }
}