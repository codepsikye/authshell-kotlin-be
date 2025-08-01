package io.cpk.be.basic.entity

import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class AppUserTest {

    @Test
    fun `should create AppUser with required parameters`() {
        // Given
        val id = "user123"
        val orgId = 1
        val username = "testuser"
        val fullname = "Test User"
        val email = "test@example.com"
        
        // When
        val appUser = AppUser(
            id = id,
            orgId = orgId,
            username = username,
            fullname = fullname,
            email = email
        )
        
        // Then
        assertEquals(id, appUser.id)
        assertEquals(orgId, appUser.orgId)
        assertEquals(username, appUser.username)
        assertEquals(fullname, appUser.fullname)
        assertEquals(email, appUser.email)
        assertNull(appUser.title)
        assertNull(appUser.password)
        assertEquals(false, appUser.orgAdmin)
        assertNotNull(appUser.createdAt)
        assertNotNull(appUser.updatedAt)
    }
    
    @Test
    fun `should create AppUser with all parameters`() {
        // Given
        val id = "user123"
        val orgId = 1
        val username = "testuser"
        val fullname = "Test User"
        val title = "Manager"
        val email = "test@example.com"
        val password = "encoded_password"
        val orgAdmin = true
        
        // When
        val appUser = AppUser(
            id = id,
            orgId = orgId,
            username = username,
            fullname = fullname,
            title = title,
            email = email,
            password = password,
            orgAdmin = orgAdmin
        )
        
        // Then
        assertEquals(id, appUser.id)
        assertEquals(orgId, appUser.orgId)
        assertEquals(username, appUser.username)
        assertEquals(fullname, appUser.fullname)
        assertEquals(title, appUser.title)
        assertEquals(email, appUser.email)
        assertEquals(password, appUser.password)
        assertEquals(orgAdmin, appUser.orgAdmin)
        assertNotNull(appUser.createdAt)
        assertNotNull(appUser.updatedAt)
    }
    
    @Test
    fun `should use default values for optional parameters if not provided`() {
        // Given
        val id = "user123"
        val orgId = 1
        val username = "testuser"
        val fullname = "Test User"
        val email = "test@example.com"
        val beforeCreation = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS)
        
        // When
        val appUser = AppUser(
            id = id,
            orgId = orgId,
            username = username,
            fullname = fullname,
            email = email
        )
        
        // Then
        val afterCreation = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS)
        
        assertNull(appUser.title)
        assertNull(appUser.password)
        assertEquals(false, appUser.orgAdmin)
        
        // Check that timestamps are between beforeCreation and afterCreation
        assert(appUser.createdAt.truncatedTo(ChronoUnit.SECONDS) >= beforeCreation)
        assert(appUser.createdAt.truncatedTo(ChronoUnit.SECONDS) <= afterCreation)
        assert(appUser.updatedAt.truncatedTo(ChronoUnit.SECONDS) >= beforeCreation)
        assert(appUser.updatedAt.truncatedTo(ChronoUnit.SECONDS) <= afterCreation)
    }
    
    @Test
    fun `should correctly implement equals and hashCode`() {
        // Given
        val appUser1 = AppUser(
            id = "user123",
            orgId = 1,
            username = "testuser",
            fullname = "Test User",
            email = "test@example.com"
        )
        val appUser2 = AppUser(
            id = "user123",
            orgId = 1,
            username = "testuser",
            fullname = "Test User",
            email = "test@example.com"
        )
        val appUser3 = AppUser(
            id = "user456",
            orgId = 1,
            username = "testuser",
            fullname = "Test User",
            email = "test@example.com"
        )
        
        // Then
        assertEquals(appUser1, appUser2)
        assertEquals(appUser1.hashCode(), appUser2.hashCode())
        assertNotEquals(appUser1, appUser3)
        assertNotEquals(appUser1.hashCode(), appUser3.hashCode())
    }
    
    @Test
    fun `should correctly implement copy`() {
        // Given
        val appUser = AppUser(
            id = "user123",
            orgId = 1,
            username = "testuser",
            fullname = "Test User",
            email = "test@example.com"
        )
        
        // When
        val copied = appUser.copy(
            username = "newusername",
            fullname = "New User",
            title = "Director"
        )
        
        // Then
        assertEquals("user123", copied.id)
        assertEquals(1, copied.orgId)
        assertEquals("newusername", copied.username)
        assertEquals("New User", copied.fullname)
        assertEquals("Director", copied.title)
        assertEquals("test@example.com", copied.email)
        assertEquals(appUser.password, copied.password)
        assertEquals(appUser.orgAdmin, copied.orgAdmin)
        assertNotNull(copied.createdAt)
        assertNotNull(copied.updatedAt)
    }
}