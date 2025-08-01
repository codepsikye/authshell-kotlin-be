package io.cpk.be.basic.dto

import jakarta.validation.Validation
import jakarta.validation.Validator
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class LoginRequestTest {
    
    private lateinit var validator: Validator
    
    @BeforeEach
    fun setUp() {
        val factory = Validation.buildDefaultValidatorFactory()
        validator = factory.validator
    }

    @Test
    fun `should create LoginRequest with required parameters`() {
        // Given
        val username = "testuser"
        val password = "password123"
        
        // When
        val loginRequest = LoginRequest(
            username = username,
            password = password
        )
        
        // Then
        assertEquals(username, loginRequest.username)
        assertEquals(password, loginRequest.password)
    }
    
    @Test
    fun `should correctly implement equals and hashCode`() {
        // Given
        val loginRequest1 = LoginRequest(
            username = "testuser",
            password = "password123"
        )
        
        val loginRequest2 = LoginRequest(
            username = "testuser",
            password = "password123"
        )
        
        val loginRequest3 = LoginRequest(
            username = "otheruser",
            password = "password123"
        )
        
        // Then
        assertEquals(loginRequest1, loginRequest2)
        assertEquals(loginRequest1.hashCode(), loginRequest2.hashCode())
        assertNotEquals(loginRequest1, loginRequest3)
        assertNotEquals(loginRequest1.hashCode(), loginRequest3.hashCode())
    }
    
    @Test
    fun `should correctly implement copy`() {
        // Given
        val loginRequest = LoginRequest(
            username = "testuser",
            password = "password123"
        )
        
        // When
        val copied = loginRequest.copy(
            password = "newpassword456"
        )
        
        // Then
        assertEquals("testuser", copied.username)
        assertEquals("newpassword456", copied.password)
    }
    
    @Test
    fun `should validate username is not blank`() {
        // Given
        val loginRequest = LoginRequest(
            username = "",
            password = "password123"
        )
        
        // When
        val violations = validator.validate(loginRequest)
        
        // Then
        assertEquals(1, violations.size)
        assertEquals("Username cannot be blank", violations.first().message)
    }
    
    @Test
    fun `should validate password is not blank`() {
        // Given
        val loginRequest = LoginRequest(
            username = "testuser",
            password = ""
        )
        
        // When
        val violations = validator.validate(loginRequest)
        
        // Then
        assertEquals(1, violations.size)
        assertEquals("Password cannot be blank", violations.first().message)
    }
    
    @Test
    fun `should validate both fields are not blank`() {
        // Given
        val loginRequest = LoginRequest(
            username = "",
            password = ""
        )
        
        // When
        val violations = validator.validate(loginRequest)
        
        // Then
        assertEquals(2, violations.size)
        val messages = violations.map { it.message }.toSet()
        assertEquals(setOf("Username cannot be blank", "Password cannot be blank"), messages)
    }
    
    @Test
    fun `should pass validation with valid data`() {
        // Given
        val loginRequest = LoginRequest(
            username = "testuser",
            password = "password123"
        )
        
        // When
        val violations = validator.validate(loginRequest)
        
        // Then
        assertEquals(0, violations.size)
    }
}