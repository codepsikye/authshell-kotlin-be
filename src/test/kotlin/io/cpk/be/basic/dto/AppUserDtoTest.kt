package io.cpk.be.basic.dto

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertNull

class AppUserDtoTest {

    @Test
    fun `should create AppUserDto with required parameters`() {
        // Given
        val id = "user123"
        val orgId = 1
        val username = "testuser"
        val fullname = "Test User"
        val email = "test@example.com"
        
        // When
        val appUserDto = AppUserDto(
            id = id,
            orgId = orgId,
            username = username,
            fullname = fullname,
            email = email
        )
        
        // Then
        assertEquals(id, appUserDto.id)
        assertEquals(orgId, appUserDto.orgId)
        assertEquals(username, appUserDto.username)
        assertEquals(fullname, appUserDto.fullname)
        assertEquals(email, appUserDto.email)
        assertNull(appUserDto.title)
        assertNull(appUserDto.password)
        assertEquals(false, appUserDto.orgAdmin)
        assertEquals(emptyMap<String, Any>(), appUserDto.userPrefs)
    }
    
    @Test
    fun `should create AppUserDto with all parameters`() {
        // Given
        val id = "user123"
        val orgId = 1
        val username = "testuser"
        val fullname = "Test User"
        val title = "Developer"
        val email = "test@example.com"
        val password = "password123"
        val orgAdmin = true
        val userPrefs = mapOf("theme" to "dark", "notifications" to true)
        
        // When
        val appUserDto = AppUserDto(
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
        assertEquals(id, appUserDto.id)
        assertEquals(orgId, appUserDto.orgId)
        assertEquals(username, appUserDto.username)
        assertEquals(fullname, appUserDto.fullname)
        assertEquals(title, appUserDto.title)
        assertEquals(email, appUserDto.email)
        assertEquals(password, appUserDto.password)
        assertEquals(orgAdmin, appUserDto.orgAdmin)
        assertEquals(userPrefs, appUserDto.userPrefs)
    }
    
    @Test
    fun `should correctly implement equals and hashCode`() {
        // Given
        val userPrefs1 = mapOf("theme" to "dark")
        val userPrefs2 = mapOf("theme" to "light")
        
        val appUserDto1 = AppUserDto(
            id = "user123",
            orgId = 1,
            username = "testuser",
            fullname = "Test User",
            email = "test@example.com",
            userPrefs = userPrefs1
        )
        
        val appUserDto2 = AppUserDto(
            id = "user123",
            orgId = 1,
            username = "testuser",
            fullname = "Test User",
            email = "test@example.com",
            userPrefs = userPrefs1
        )
        
        val appUserDto3 = AppUserDto(
            id = "user456",
            orgId = 1,
            username = "testuser",
            fullname = "Test User",
            email = "test@example.com",
            userPrefs = userPrefs1
        )
        
        val appUserDto4 = AppUserDto(
            id = "user123",
            orgId = 1,
            username = "testuser",
            fullname = "Test User",
            email = "test@example.com",
            userPrefs = userPrefs2
        )
        
        // Then
        assertEquals(appUserDto1, appUserDto2)
        assertEquals(appUserDto1.hashCode(), appUserDto2.hashCode())
        assertNotEquals(appUserDto1, appUserDto3)
        assertNotEquals(appUserDto1.hashCode(), appUserDto3.hashCode())
        assertNotEquals(appUserDto1, appUserDto4)
        assertNotEquals(appUserDto1.hashCode(), appUserDto4.hashCode())
    }
    
    @Test
    fun `should correctly implement copy`() {
        // Given
        val initialUserPrefs = mapOf("theme" to "dark", "notifications" to true)
        val appUserDto = AppUserDto(
            id = "user123",
            orgId = 1,
            username = "testuser",
            fullname = "Test User",
            email = "test@example.com",
            userPrefs = initialUserPrefs
        )
        
        // When - copy with some changes but keep userPrefs
        val copied1 = appUserDto.copy(
            fullname = "Updated User",
            email = "updated@example.com"
        )
        
        // When - copy with new userPrefs
        val newUserPrefs = mapOf("theme" to "light", "notifications" to false)
        val copied2 = appUserDto.copy(
            username = "newusername",
            userPrefs = newUserPrefs
        )
        
        // Then - check copied1
        assertEquals("user123", copied1.id)
        assertEquals(1, copied1.orgId)
        assertEquals("testuser", copied1.username)
        assertEquals("Updated User", copied1.fullname)
        assertEquals("updated@example.com", copied1.email)
        assertEquals(initialUserPrefs, copied1.userPrefs)
        
        // Then - check copied2
        assertEquals("user123", copied2.id)
        assertEquals("newusername", copied2.username)
        assertEquals("Test User", copied2.fullname)
        assertEquals(newUserPrefs, copied2.userPrefs)
    }
    
    @Test
    fun `should convert between UserPrefs and Map`() {
        // Given
        val userPrefsMap = mapOf("theme" to "dark", "notifications" to true)
        val userPrefs = UserPrefs.fromMap(userPrefsMap)
        
        // When
        val appUserDto = AppUserDto.fromUserPrefs(
            id = "user123",
            orgId = 1,
            username = "testuser",
            fullname = "Test User",
            email = "test@example.com",
            userPrefs = userPrefs
        )
        
        // Then
        assertEquals(userPrefsMap, appUserDto.userPrefs)
        
        // When
        val convertedUserPrefs = appUserDto.getUserPrefsObject()
        
        // Then
        assertEquals(userPrefs.getProperty("theme"), convertedUserPrefs.getProperty("theme"))
        assertEquals(userPrefs.getProperty("notifications"), convertedUserPrefs.getProperty("notifications"))
        assertEquals(userPrefs.toMap(), convertedUserPrefs.toMap())
    }
}