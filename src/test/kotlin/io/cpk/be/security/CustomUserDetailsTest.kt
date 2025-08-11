package io.cpk.be.security

import org.junit.jupiter.api.Test
import org.springframework.security.core.authority.SimpleGrantedAuthority
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

class CustomUserDetailsTest {

    @Test
    fun `should return correct username`() {
        // Given
        val username = "testuser"
        val userDetails = CustomUserDetails(
            username = username,
            password = "password",
            authorities = emptyList(),
            orgId = 1
        )

        // When & Then
        assertEquals(username, userDetails.username)
    }

    @Test
    fun `should return correct password`() {
        // Given
        val password = "password123"
        val userDetails = CustomUserDetails(
            username = "testuser",
            password = password,
            authorities = emptyList(),
            orgId = 1
        )

        // When & Then
        assertEquals(password, userDetails.password)
    }

    @Test
    fun `should return correct authorities`() {
        // Given
        val authorities = listOf(
            SimpleGrantedAuthority("READ"),
            SimpleGrantedAuthority("WRITE")
        )
        val userDetails = CustomUserDetails(
            username = "testuser",
            password = "password",
            authorities = authorities,
            orgId = 1
        )

        // When & Then
        assertEquals(authorities, userDetails.authorities)
        assertEquals(2, userDetails.authorities.size)
        assertTrue(userDetails.authorities.any { it.authority == "READ" })
        assertTrue(userDetails.authorities.any { it.authority == "WRITE" })
    }

    @Test
    fun `should return correct orgId`() {
        // Given
        val orgId = 42
        val userDetails = CustomUserDetails(
            username = "testuser",
            password = "password",
            authorities = emptyList(),
            orgId = orgId
        )

        // When & Then
        assertEquals(orgId, userDetails.orgId)
    }

    @Test
    fun `should return correct centerId when provided`() {
        // Given
        val centerId = 123
        val userDetails = CustomUserDetails(
            username = "testuser",
            password = "password",
            authorities = emptyList(),
            orgId = 1,
            centerId = centerId
        )

        // When & Then
        assertEquals(centerId, userDetails.centerId)
    }

    @Test
    fun `should return correct id when provided`() {
        // Given
        val id = 123
        val userDetails = CustomUserDetails(
            username = "testuser",
            password = "password",
            authorities = emptyList(),
            orgId = 1,
            id = id
        )

        // When & Then
        assertEquals(id, userDetails.id)
    }

    @Test
    fun `should return true for all account status methods`() {
        // Given
        val userDetails = CustomUserDetails(
            username = "testuser",
            password = "password",
            authorities = emptyList(),
            orgId = 1
        )

        // When & Then
        assertTrue(userDetails.isAccountNonExpired)
        assertTrue(userDetails.isAccountNonLocked)
        assertTrue(userDetails.isCredentialsNonExpired)
        assertTrue(userDetails.isEnabled)
    }

    @Test
    fun `should use default equals and hashCode behavior`() {
        // Given
        val userDetails1 = CustomUserDetails(
            username = "testuser",
            password = "password",
            authorities = emptyList(),
            orgId = 1
        )
        
        val userDetails2 = CustomUserDetails(
            username = "testuser",
            password = "password",
            authorities = emptyList(),
            orgId = 1
        )
        
        // When & Then
        // Since equals is not overridden, different instances should not be equal
        assertNotEquals(userDetails1, userDetails2)
        
        // Same instance should be equal to itself
        assertEquals(userDetails1, userDetails1)
    }
}