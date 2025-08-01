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
        
        // When
        val appUserDto = AppUserDto(
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
        assertEquals(id, appUserDto.id)
        assertEquals(orgId, appUserDto.orgId)
        assertEquals(username, appUserDto.username)
        assertEquals(fullname, appUserDto.fullname)
        assertEquals(title, appUserDto.title)
        assertEquals(email, appUserDto.email)
        assertEquals(password, appUserDto.password)
        assertEquals(orgAdmin, appUserDto.orgAdmin)
    }
    
    @Test
    fun `should correctly implement equals and hashCode`() {
        // Given
        val appUserDto1 = AppUserDto(
            id = "user123",
            orgId = 1,
            username = "testuser",
            fullname = "Test User",
            email = "test@example.com"
        )
        
        val appUserDto2 = AppUserDto(
            id = "user123",
            orgId = 1,
            username = "testuser",
            fullname = "Test User",
            email = "test@example.com"
        )
        
        val appUserDto3 = AppUserDto(
            id = "user456",
            orgId = 1,
            username = "testuser",
            fullname = "Test User",
            email = "test@example.com"
        )
        
        // Then
        assertEquals(appUserDto1, appUserDto2)
        assertEquals(appUserDto1.hashCode(), appUserDto2.hashCode())
        assertNotEquals(appUserDto1, appUserDto3)
        assertNotEquals(appUserDto1.hashCode(), appUserDto3.hashCode())
    }
    
    @Test
    fun `should correctly implement copy`() {
        // Given
        val appUserDto = AppUserDto(
            id = "user123",
            orgId = 1,
            username = "testuser",
            fullname = "Test User",
            email = "test@example.com"
        )
        
        // When
        val copied = appUserDto.copy(
            fullname = "Updated User",
            email = "updated@example.com"
        )
        
        // Then
        assertEquals("user123", copied.id)
        assertEquals(1, copied.orgId)
        assertEquals("testuser", copied.username)
        assertEquals("Updated User", copied.fullname)
        assertEquals("updated@example.com", copied.email)
    }
}