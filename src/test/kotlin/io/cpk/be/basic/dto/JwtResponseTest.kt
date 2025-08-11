package io.cpk.be.basic.dto

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertNull

class JwtResponseTest {

    @Test
    fun `should create JwtResponse with required parameters`() {
        // Given
        val accessToken = "access-token-123"
        val refreshToken = "refresh-token-456"
        val expiresIn = 3600L
        val user = UserInfo(
            id = 123,
            username = "testuser",
            fullname = "Test User",
            email = "test@example.com",
            orgId = 1,
            centerId = 2,
            orgAdmin = false,
            accessRight = listOf("READ", "WRITE")
        )
        
        // When
        val jwtResponse = JwtResponse(
            accessToken = accessToken,
            refreshToken = refreshToken,
            expiresIn = expiresIn,
            user = user
        )
        
        // Then
        assertEquals(accessToken, jwtResponse.accessToken)
        assertEquals(refreshToken, jwtResponse.refreshToken)
        assertEquals("Bearer", jwtResponse.tokenType) // Default value
        assertEquals(expiresIn, jwtResponse.expiresIn)
        assertEquals(user, jwtResponse.user)
    }
    
    @Test
    fun `should create JwtResponse with custom tokenType`() {
        // Given
        val accessToken = "access-token-123"
        val refreshToken = "refresh-token-456"
        val tokenType = "Custom"
        val expiresIn = 3600L
        val user = UserInfo(
            id = 123,
            username = "testuser",
            fullname = "Test User",
            email = "test@example.com",
            orgId = 1,
            centerId = 2,
            orgAdmin = false,
            accessRight = listOf("READ", "WRITE")
        )
        
        // When
        val jwtResponse = JwtResponse(
            accessToken = accessToken,
            refreshToken = refreshToken,
            tokenType = tokenType,
            expiresIn = expiresIn,
            user = user
        )
        
        // Then
        assertEquals(accessToken, jwtResponse.accessToken)
        assertEquals(refreshToken, jwtResponse.refreshToken)
        assertEquals(tokenType, jwtResponse.tokenType)
        assertEquals(expiresIn, jwtResponse.expiresIn)
        assertEquals(user, jwtResponse.user)
    }
    
    @Test
    fun `should correctly implement equals and hashCode for JwtResponse`() {
        // Given
        val user = UserInfo(
            id = 123,
            username = "testuser",
            fullname = "Test User",
            email = "test@example.com",
            orgId = 1,
            centerId = 2,
            orgAdmin = false,
            accessRight = listOf("READ", "WRITE")
        )
        
        val jwtResponse1 = JwtResponse(
            accessToken = "access-token-123",
            refreshToken = "refresh-token-456",
            expiresIn = 3600L,
            user = user
        )
        
        val jwtResponse2 = JwtResponse(
            accessToken = "access-token-123",
            refreshToken = "refresh-token-456",
            expiresIn = 3600L,
            user = user
        )
        
        val jwtResponse3 = JwtResponse(
            accessToken = "different-token",
            refreshToken = "refresh-token-456",
            expiresIn = 3600L,
            user = user
        )
        
        // Then
        assertEquals(jwtResponse1, jwtResponse2)
        assertEquals(jwtResponse1.hashCode(), jwtResponse2.hashCode())
        assertNotEquals(jwtResponse1, jwtResponse3)
        assertNotEquals(jwtResponse1.hashCode(), jwtResponse3.hashCode())
    }
    
    @Test
    fun `should correctly implement copy for JwtResponse`() {
        // Given
        val user = UserInfo(
            id = 123,
            username = "testuser",
            fullname = "Test User",
            email = "test@example.com",
            orgId = 1,
            centerId = 2,
            orgAdmin = false,
            accessRight = listOf("READ", "WRITE")
        )
        
        val jwtResponse = JwtResponse(
            accessToken = "access-token-123",
            refreshToken = "refresh-token-456",
            expiresIn = 3600L,
            user = user
        )
        
        // When
        val copied = jwtResponse.copy(
            accessToken = "new-access-token",
            tokenType = "Custom"
        )
        
        // Then
        assertEquals("new-access-token", copied.accessToken)
        assertEquals("refresh-token-456", copied.refreshToken)
        assertEquals("Custom", copied.tokenType)
        assertEquals(3600L, copied.expiresIn)
        assertEquals(user, copied.user)
    }
    
    @Test
    fun `should create UserInfo with required parameters`() {
        // Given
        val id = 123
        val username = "testuser"
        val fullname = "Test User"
        val email = "test@example.com"
        val orgId = 1
        val centerId = 2
        val orgAdmin = false
        val accessRight = listOf("READ", "WRITE")
        
        // When
        val userInfo = UserInfo(
            id = id,
            username = username,
            fullname = fullname,
            email = email,
            orgId = orgId,
            centerId = centerId,
            orgAdmin = orgAdmin,
            accessRight = accessRight
        )
        
        // Then
        assertEquals(id, userInfo.id)
        assertEquals(username, userInfo.username)
        assertEquals(fullname, userInfo.fullname)
        assertEquals(email, userInfo.email)
        assertEquals(orgId, userInfo.orgId)
        assertEquals(centerId, userInfo.centerId)
        assertEquals(orgAdmin, userInfo.orgAdmin)
        assertEquals(accessRight, userInfo.accessRight)
    }
    
    @Test
    fun `should create UserInfo with null centerId`() {
        // Given
        val id = 123
        val username = "testuser"
        val fullname = "Test User"
        val email = "test@example.com"
        val orgId = 1
        val orgAdmin = false
        val accessRight = listOf("READ", "WRITE")
        
        // When
        val userInfo = UserInfo(
            id = id,
            username = username,
            fullname = fullname,
            email = email,
            orgId = orgId,
            centerId = null,
            orgAdmin = orgAdmin,
            accessRight = accessRight
        )
        
        // Then
        assertEquals(id, userInfo.id)
        assertEquals(username, userInfo.username)
        assertEquals(fullname, userInfo.fullname)
        assertEquals(email, userInfo.email)
        assertEquals(orgId, userInfo.orgId)
        assertNull(userInfo.centerId)
        assertEquals(orgAdmin, userInfo.orgAdmin)
        assertEquals(accessRight, userInfo.accessRight)
    }
    
    @Test
    fun `should correctly implement equals and hashCode for UserInfo`() {
        // Given
        val userInfo1 = UserInfo(
            id = 123,
            username = "testuser",
            fullname = "Test User",
            email = "test@example.com",
            orgId = 1,
            centerId = 2,
            orgAdmin = false,
            accessRight = listOf("READ", "WRITE")
        )
        
        val userInfo2 = UserInfo(
            id = 123,
            username = "testuser",
            fullname = "Test User",
            email = "test@example.com",
            orgId = 1,
            centerId = 2,
            orgAdmin = false,
            accessRight = listOf("READ", "WRITE")
        )
        
        val userInfo3 = UserInfo(
            id = 456,
            username = "testuser",
            fullname = "Test User",
            email = "test@example.com",
            orgId = 1,
            centerId = 2,
            orgAdmin = false,
            accessRight = listOf("READ", "WRITE")
        )
        
        // Then
        assertEquals(userInfo1, userInfo2)
        assertEquals(userInfo1.hashCode(), userInfo2.hashCode())
        assertNotEquals(userInfo1, userInfo3)
        assertNotEquals(userInfo1.hashCode(), userInfo3.hashCode())
    }
    
    @Test
    fun `should correctly implement copy for UserInfo`() {
        // Given
        val userInfo = UserInfo(
            id = 123,
            username = "testuser",
            fullname = "Test User",
            email = "test@example.com",
            orgId = 1,
            centerId = 2,
            orgAdmin = false,
            accessRight = listOf("READ", "WRITE")
        )
        
        // When
        val copied = userInfo.copy(
            fullname = "Updated User",
            orgAdmin = true,
            accessRight = listOf("READ", "WRITE", "DELETE")
        )
        
        // Then
        assertEquals(123, copied.id)
        assertEquals("testuser", copied.username)
        assertEquals("Updated User", copied.fullname)
        assertEquals("test@example.com", copied.email)
        assertEquals(1, copied.orgId)
        assertEquals(2, copied.centerId)
        assertEquals(true, copied.orgAdmin)
        assertEquals(listOf("READ", "WRITE", "DELETE"), copied.accessRight)
    }
}