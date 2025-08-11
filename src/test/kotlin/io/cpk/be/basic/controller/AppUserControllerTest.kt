package io.cpk.be.basic.controller

import io.cpk.be.basic.dto.AppUserDto
import io.cpk.be.basic.service.AppUserService
import io.cpk.be.security.CustomUserDetails
import io.mockk.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.http.HttpStatus
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

/**
 * Unit tests for AppUserController
 */
class AppUserControllerTest {

    private lateinit var appUserService: AppUserService
    private lateinit var appUserController: AppUserController

    @BeforeEach
    fun setUp() {
        appUserService = mockk()
        appUserController = AppUserController(appUserService)
    }

    @Test
    fun `should create app user successfully`() {
        // Given
        val appUserDto = AppUserDto(
            id = 0, // Temporary ID that will be replaced by the service
            username = "testuser",
            fullname = "Test User",
            email = "test@example.com",
            orgId = 1,
            orgAdmin = false,
            password = "password123"
        )
        
        val createdAppUserDto = appUserDto.copy(id = 1) // Service generates a new ID
        
        every { appUserService.create(any()) } returns createdAppUserDto

        // When
        val response = appUserController.create(appUserDto)

        // Then
        assertEquals(HttpStatus.CREATED, response.statusCode)
        assertNotNull(response.body)
        assertEquals(1, response.body?.id)
        assertEquals("testuser", response.body?.username)
        assertEquals("Test User", response.body?.fullname)
        assertEquals("test@example.com", response.body?.email)
        assertEquals(1, response.body?.orgId)
        assertEquals(false, response.body?.orgAdmin)
        
        verify(exactly = 1) { appUserService.create(appUserDto) }
    }

    @Test
    fun `should find all app users with pagination`() {
        // Given
        val page = 0
        val size = 10
        val pageable = PageRequest.of(page, size)
        val orgId = 1
        val fullname: String? = null
        
        val appUserDto1 = AppUserDto(id = 1, username = "user1", fullname = "User One", email = "user1@example.com", orgId = orgId, orgAdmin = false)
        val appUserDto2 = AppUserDto(id = 2, username = "user2", fullname = "User Two", email = "user2@example.com", orgId = orgId, orgAdmin = false)
        val users = listOf(appUserDto1, appUserDto2)
        
        val pageResult = PageImpl(users, pageable, users.size.toLong())
        
        val userDetails = mockk<CustomUserDetails>()
        every { userDetails.orgId } returns orgId
        every { appUserService.findAll(orgId, fullname, pageable) } returns pageResult

        // When
        val response = appUserController.findAll(page, size, fullname, userDetails)

        // Then
        assertEquals(HttpStatus.OK, response.statusCode)
        assertNotNull(response.body)
        assertEquals(2, response.body?.content?.size)
        assertEquals(2, response.body?.totalElements)
        
        verify(exactly = 1) { appUserService.findAll(orgId, fullname, pageable) }
    }

    @Test
    fun `should find app user by id when user exists`() {
        // Given
        val userId = 123
        val orgId = 1
        val appUserDto = AppUserDto(
            id = userId,
            username = "testuser",
            fullname = "Test User",
            email = "test@example.com",
            orgId = orgId,
            orgAdmin = false
        )
        
        val userDetails = mockk<CustomUserDetails>()
        every { userDetails.orgId } returns orgId
        every { appUserService.findById(userId) } returns appUserDto

        // When
        val response = appUserController.findById(userId, userDetails)

        // Then
        assertEquals(HttpStatus.OK, response.statusCode)
        assertNotNull(response.body)
        assertEquals(userId, response.body?.id)
        assertEquals("testuser", response.body?.username)
        assertEquals("Test User", response.body?.fullname)
        assertEquals("test@example.com", response.body?.email)
        
        verify(exactly = 1) { appUserService.findById(userId) }
    }

    @Test
    fun `should return not found when app user does not exist`() {
        // Given
        val userId = 999
        val orgId = 1
        
        val userDetails = mockk<CustomUserDetails>()
        every { userDetails.orgId } returns orgId
        every { appUserService.findById(userId) } returns null

        // When
        val response = appUserController.findById(userId, userDetails)

        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
        assertNull(response.body)
        
        verify(exactly = 1) { appUserService.findById(userId) }
    }

    @Test
    fun `should return not found when app user exists but belongs to different org`() {
        // Given
        val userId = 123
        val userOrgId = 1
        val currentUserOrgId = 2
        
        val appUserDto = AppUserDto(
            id = userId,
            username = "testuser",
            fullname = "Test User",
            email = "test@example.com",
            orgId = userOrgId,
            orgAdmin = false
        )
        
        val userDetails = mockk<CustomUserDetails>()
        every { userDetails.orgId } returns currentUserOrgId
        every { appUserService.findById(userId) } returns appUserDto

        // When
        val response = appUserController.findById(userId, userDetails)

        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
        assertNull(response.body)
        
        verify(exactly = 1) { appUserService.findById(userId) }
    }

    @Test
    fun `should update app user successfully`() {
        // Given
        val userId = 123
        val appUserDto = AppUserDto(
            id = userId,
            username = "updateduser",
            fullname = "Updated User",
            email = "updated@example.com",
            orgId = 1,
            orgAdmin = true
        )
        
        every { appUserService.update(userId, appUserDto) } returns appUserDto

        // When
        val response = appUserController.update(userId, appUserDto)

        // Then
        assertEquals(HttpStatus.OK, response.statusCode)
        assertNotNull(response.body)
        assertEquals(userId, response.body?.id)
        assertEquals("updateduser", response.body?.username)
        assertEquals("Updated User", response.body?.fullname)
        assertEquals("updated@example.com", response.body?.email)
        assertEquals(true, response.body?.orgAdmin)
        
        verify(exactly = 1) { appUserService.update(userId, appUserDto) }
    }

    @Test
    fun `should delete app user successfully`() {
        // Given
        val userId = 123
        
        every { appUserService.delete(userId) } just runs

        // When
        val response = appUserController.delete(userId)

        // Then
        assertEquals(HttpStatus.NO_CONTENT, response.statusCode)
        assertNull(response.body)
        
        verify(exactly = 1) { appUserService.delete(userId) }
    }
}