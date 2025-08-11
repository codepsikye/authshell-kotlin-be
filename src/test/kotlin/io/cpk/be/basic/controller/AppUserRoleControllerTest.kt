package io.cpk.be.basic.controller

import io.cpk.be.basic.dto.AppUserRoleDto
import io.cpk.be.basic.service.AppUserRoleService
import io.mockk.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.http.HttpStatus
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class AppUserRoleControllerTest {

    private lateinit var appUserRoleService: AppUserRoleService
    private lateinit var appUserRoleController: AppUserRoleController

    @BeforeEach
    fun setUp() {
        appUserRoleService = mockk()
        appUserRoleController = AppUserRoleController(appUserRoleService)
    }

    @Test
    fun `should create app user role successfully`() {
        // Given
        val appUserRoleDto = AppUserRoleDto(
            userId = 123,
            orgId = 1,
            centerId = 1,
            roleName = "admin"
        )
        
        every { appUserRoleService.create(any()) } returns appUserRoleDto
        
        // When
        val response = appUserRoleController.create(appUserRoleDto)
        
        // Then
        assertEquals(HttpStatus.CREATED, response.statusCode)
        assertNotNull(response.body)
        assertEquals(123, response.body?.userId)
        assertEquals(1, response.body?.orgId)
        assertEquals(1, response.body?.centerId)
        assertEquals("admin", response.body?.roleName)
        
        verify(exactly = 1) { appUserRoleService.create(appUserRoleDto) }
    }
    
    @Test
    fun `should find all app user roles with pagination`() {
        // Given
        val page = 0
        val size = 10
        val pageable = PageRequest.of(page, size)
        
        val appUserRoleDto1 = AppUserRoleDto(userId = 1, orgId = 1, centerId = 1, roleName = "admin")
        val appUserRoleDto2 = AppUserRoleDto(userId = 2, orgId = 1, centerId = 1, roleName = "user")
        val appUserRoles = listOf(appUserRoleDto1, appUserRoleDto2)
        
        val pageResult = PageImpl(appUserRoles, pageable, appUserRoles.size.toLong())
        
        every { appUserRoleService.findAll(pageable) } returns pageResult
        
        // When
        val response = appUserRoleController.findAll(page, size)
        
        // Then
        assertEquals(HttpStatus.OK, response.statusCode)
        assertNotNull(response.body)
        assertEquals(2, response.body?.content?.size)
        assertEquals(2, response.body?.totalElements)
        assertEquals(1, response.body?.content?.get(0)?.userId)
        assertEquals(2, response.body?.content?.get(1)?.userId)
        
        verify(exactly = 1) { appUserRoleService.findAll(pageable) }
    }
    
    @Test
    fun `should find app user role by id when it exists`() {
        // Given
        val userId = 123
        val orgId = 1
        val centerId = 1
        val roleName = "admin"
        
        val appUserRoleDto = AppUserRoleDto(
            userId = userId,
            orgId = orgId,
            centerId = centerId,
            roleName = roleName
        )
        
        every { appUserRoleService.findById(userId, orgId, centerId, roleName) } returns appUserRoleDto
        
        // When
        val response = appUserRoleController.findById(userId, orgId, centerId, roleName)
        
        // Then
        assertEquals(HttpStatus.OK, response.statusCode)
        assertNotNull(response.body)
        assertEquals(userId, response.body?.userId)
        assertEquals(orgId, response.body?.orgId)
        assertEquals(centerId, response.body?.centerId)
        assertEquals(roleName, response.body?.roleName)
        
        verify(exactly = 1) { appUserRoleService.findById(userId, orgId, centerId, roleName) }
    }
    
    @Test
    fun `should return not found when app user role does not exist`() {
        // Given
        val userId = 999
        val orgId = 999
        val centerId = 999
        val roleName = "non-existent-role"
        
        every { appUserRoleService.findById(userId, orgId, centerId, roleName) } returns null
        
        // When
        val response = appUserRoleController.findById(userId, orgId, centerId, roleName)
        
        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
        assertNull(response.body)
        
        verify(exactly = 1) { appUserRoleService.findById(userId, orgId, centerId, roleName) }
    }
    
    @Test
    fun `should update app user role successfully`() {
        // Given
        val userId = 123
        val orgId = 1
        val centerId = 1
        val roleName = "admin"
        
        val appUserRoleDto = AppUserRoleDto(
            userId = userId,
            orgId = orgId,
            centerId = centerId,
            roleName = roleName
        )
        
        every { 
            appUserRoleService.update(userId, orgId, centerId, roleName, appUserRoleDto) 
        } returns appUserRoleDto
        
        // When
        val response = appUserRoleController.update(userId, orgId, centerId, roleName, appUserRoleDto)
        
        // Then
        assertEquals(HttpStatus.OK, response.statusCode)
        assertNotNull(response.body)
        assertEquals(userId, response.body?.userId)
        assertEquals(orgId, response.body?.orgId)
        assertEquals(centerId, response.body?.centerId)
        assertEquals(roleName, response.body?.roleName)
        
        verify(exactly = 1) { 
            appUserRoleService.update(userId, orgId, centerId, roleName, appUserRoleDto) 
        }
    }
    
    @Test
    fun `should delete app user role successfully`() {
        // Given
        val userId = 123
        val orgId = 1
        val centerId = 1
        val roleName = "admin"
        
        every { appUserRoleService.delete(userId, orgId, centerId, roleName) } just runs
        
        // When
        val response = appUserRoleController.delete(userId, orgId, centerId, roleName)
        
        // Then
        assertEquals(HttpStatus.NO_CONTENT, response.statusCode)
        assertNull(response.body)
        
        verify(exactly = 1) { appUserRoleService.delete(userId, orgId, centerId, roleName) }
    }
}