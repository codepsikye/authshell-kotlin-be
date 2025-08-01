package io.cpk.be.basic.entity

import io.cpk.be.basic.dto.UserPrefs
import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

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
        val appUser = AppUser.create(
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
        // UserPrefs should be empty by default
        assertTrue(appUser.userPrefs.toMap().isEmpty())
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
        val userPrefsMap = mapOf("theme" to "dark", "notifications" to true)
        val userPrefs = UserPrefs.fromMap(userPrefsMap)
        
        // When
        val appUser = AppUser.create(
            id = id,
            orgId = orgId,
            username = username,
            fullname = fullname,
            title = title,
            email = email,
            password = password,
            orgAdmin = orgAdmin,
            userPrefs = userPrefs
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
        assertEquals(userPrefsMap, appUser.userPrefs.toMap())
    }
    
    @Test
    fun `should use default values for optional parameters if not provided`() {
        // Given
        val id = "user123"
        val orgId = 1
        val username = "testuser"
        val fullname = "Test User"
        val email = "test@example.com"
        
        // When
        val appUser = AppUser.create(
            id = id,
            orgId = orgId,
            username = username,
            fullname = fullname,
            email = email
        )
        
        // Then
        assertNull(appUser.title)
        assertNull(appUser.password)
        assertEquals(false, appUser.orgAdmin)
        // UserPrefs should be empty by default
        assertTrue(appUser.userPrefs.toMap().isEmpty())
    }
    
    @Test
    fun `should correctly implement equals and hashCode`() {
        // Given
        val userPrefsMap1 = mapOf("theme" to "dark")
        val userPrefsMap2 = mapOf("theme" to "light")
        val userPrefs1 = UserPrefs.fromMap(userPrefsMap1)
        val userPrefs2 = UserPrefs.fromMap(userPrefsMap2)
        
        val appUser1 = AppUser.create(
            id = "user123",
            orgId = 1,
            username = "testuser",
            fullname = "Test User",
            email = "test@example.com",
            userPrefs = userPrefs1
        )
        val appUser2 = AppUser.create(
            id = "user123",
            orgId = 1,
            username = "testuser",
            fullname = "Test User",
            email = "test@example.com",
            userPrefs = userPrefs1
        )
        val appUser3 = AppUser.create(
            id = "user456",
            orgId = 1,
            username = "testuser",
            fullname = "Test User",
            email = "test@example.com",
            userPrefs = userPrefs1
        )
        val appUser4 = AppUser.create(
            id = "user123",
            orgId = 1,
            username = "testuser",
            fullname = "Test User",
            email = "test@example.com",
            userPrefs = userPrefs2
        )
        
        // Then
        assertEquals(appUser1, appUser2)
        assertEquals(appUser1.hashCode(), appUser2.hashCode())
        assertNotEquals(appUser1, appUser3)
        assertNotEquals(appUser1.hashCode(), appUser3.hashCode())
        assertNotEquals(appUser1, appUser4)
        assertNotEquals(appUser1.hashCode(), appUser4.hashCode())
    }
    
    @Test
    fun `should correctly implement copy`() {
        // Given
        val initialUserPrefsMap = mapOf("theme" to "dark", "notifications" to true)
        val initialUserPrefs = UserPrefs.fromMap(initialUserPrefsMap)
        val appUser = AppUser.create(
            id = "user123",
            orgId = 1,
            username = "testuser",
            fullname = "Test User",
            email = "test@example.com",
            userPrefs = initialUserPrefs
        )
        
        // When - copy with some changes but keep userPrefs
        val copied1 = appUser.copy(
            username = "newusername",
            fullname = "New User",
            title = "Director"
        )
        
        // When - copy with new userPrefs
        val newUserPrefsMap = mapOf("theme" to "light", "notifications" to false)
        val newUserPrefs = UserPrefs.fromMap(newUserPrefsMap)
        val copied2 = appUser.copy(
            username = "newusername2",
            userPrefs = newUserPrefs
        )
        
        // Then - check copied1
        assertEquals("user123", copied1.id)
        assertEquals(1, copied1.orgId)
        assertEquals("newusername", copied1.username)
        assertEquals("New User", copied1.fullname)
        assertEquals("Director", copied1.title)
        assertEquals("test@example.com", copied1.email)
        assertEquals(appUser.password, copied1.password)
        assertEquals(appUser.orgAdmin, copied1.orgAdmin)
        assertEquals(initialUserPrefsMap, copied1.userPrefs.toMap())
        
        // Then - check copied2
        assertEquals("user123", copied2.id)
        assertEquals("newusername2", copied2.username)
        assertEquals("Test User", copied2.fullname)
        assertEquals(newUserPrefsMap, copied2.userPrefs.toMap())
    }
}