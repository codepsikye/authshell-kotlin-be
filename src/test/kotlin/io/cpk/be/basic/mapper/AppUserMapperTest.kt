package io.cpk.be.basic.mapper

import io.cpk.be.basic.dto.AppUserDto
import io.cpk.be.basic.entity.AppUser
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

/**
 * Unit tests for AppUserMapper
 */
class AppUserMapperTest {

    private val appUserMapper = AppUserMapper()


    @Test
    fun `should map entity to dto`() {
        // Given
        val appUser = AppUser(
            id = "user123",
            username = "testuser",
            fullname = "Test User",
            email = "test@example.com",
            orgId = 1,
            title = "Developer",
            password = "encoded_password",
            orgAdmin = true
        )

        // When
        val appUserDto = appUserMapper.toDto(appUser)

        // Then
        assertEquals("user123", appUserDto.id)
        assertEquals("testuser", appUserDto.username)
        assertEquals("Test User", appUserDto.fullname)
        assertEquals("test@example.com", appUserDto.email)
        assertEquals(1, appUserDto.orgId)
        assertEquals("Developer", appUserDto.title)
        assertNull(appUserDto.password) // Password should be ignored in mapping
        assertEquals(true, appUserDto.orgAdmin)
    }

    @Test
    fun `should map dto to entity`() {
        // Given
        val appUserDto = AppUserDto(
            id = "user123",
            username = "testuser",
            fullname = "Test User",
            email = "test@example.com",
            orgId = 1,
            title = "Developer",
            password = "password123",
            orgAdmin = true
        )

        // When
        val appUser = appUserMapper.toEntity(appUserDto)

        // Then
        assertEquals("user123", appUser.id)
        assertEquals("testuser", appUser.username)
        assertEquals("Test User", appUser.fullname)
        assertEquals("test@example.com", appUser.email)
        assertEquals(1, appUser.orgId)
        assertEquals("Developer", appUser.title)
        assertEquals("password123", appUser.password) // Password should be mapped
        assertEquals(true, appUser.orgAdmin)
        // createdAt and updatedAt should be ignored in mapping and set to default values
    }

    @Test
    fun `should map entity list to dto list`() {
        // Given
        val appUser1 = AppUser(
            id = "user1",
            username = "user1",
            fullname = "User One",
            email = "user1@example.com",
            orgId = 1,
            password = "encoded_password1",
            orgAdmin = false
        )
        val appUser2 = AppUser(
            id = "user2",
            username = "user2",
            fullname = "User Two",
            email = "user2@example.com",
            orgId = 1,
            password = "encoded_password2",
            orgAdmin = true
        )
        val appUsers = listOf(appUser1, appUser2)

        // When
        val appUserDtos = appUserMapper.toDtoList(appUsers)

        // Then
        assertEquals(2, appUserDtos.size)
        assertEquals("user1", appUserDtos[0].id)
        assertEquals("User One", appUserDtos[0].fullname)
        assertNull(appUserDtos[0].password) // Password should be ignored in mapping
        assertEquals("user2", appUserDtos[1].id)
        assertEquals("User Two", appUserDtos[1].fullname)
        assertNull(appUserDtos[1].password) // Password should be ignored in mapping
    }

    @Test
    fun `should map dto list to entity list`() {
        // Given
        val appUserDto1 = AppUserDto(
            id = "user1",
            username = "user1",
            fullname = "User One",
            email = "user1@example.com",
            orgId = 1,
            password = "password1",
            orgAdmin = false
        )
        val appUserDto2 = AppUserDto(
            id = "user2",
            username = "user2",
            fullname = "User Two",
            email = "user2@example.com",
            orgId = 1,
            password = "password2",
            orgAdmin = true
        )
        val appUserDtos = listOf(appUserDto1, appUserDto2)

        // When
        val appUsers = appUserMapper.toEntityList(appUserDtos)

        // Then
        assertEquals(2, appUsers.size)
        assertEquals("user1", appUsers[0].id)
        assertEquals("User One", appUsers[0].fullname)
        assertEquals("password1", appUsers[0].password) // Password should be mapped
        // createdAt and updatedAt should be ignored in mapping and set to default values
        assertEquals("user2", appUsers[1].id)
        assertEquals("User Two", appUsers[1].fullname)
        assertEquals("password2", appUsers[1].password) // Password should be mapped
        // createdAt and updatedAt should be ignored in mapping and set to default values
    }

    @Test
    fun `should handle null values correctly`() {
        // Given
        val appUser = AppUser(
            id = "user123",
            username = "testuser",
            fullname = "Test User",
            email = "test@example.com",
            orgId = 1,
            title = null,
            password = null,
            orgAdmin = false
        )

        // When
        val appUserDto = appUserMapper.toDto(appUser)

        // Then
        assertEquals("user123", appUserDto.id)
        assertEquals("testuser", appUserDto.username)
        assertEquals("Test User", appUserDto.fullname)
        assertEquals("test@example.com", appUserDto.email)
        assertEquals(1, appUserDto.orgId)
        assertNull(appUserDto.title)
        assertNull(appUserDto.password)
        assertEquals(false, appUserDto.orgAdmin)
    }
}