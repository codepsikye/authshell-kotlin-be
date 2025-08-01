package io.cpk.be.basic.controller

import io.cpk.be.basic.dto.RoleDto
import io.cpk.be.basic.service.RoleService
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

class RoleControllerTest {

    private lateinit var roleService: RoleService
    private lateinit var roleController: RoleController

    @BeforeEach
    fun setUp() {
        roleService = mockk()
        roleController = RoleController(roleService)
    }

    @Test
    fun `should create role successfully`() {
        // Given
        val orgId = 1
        val roleDto = RoleDto(
            orgId = orgId,
            name = "Test Role",
            accessRight = listOf("read", "write")
        )
        
        val userDetails = mockk<CustomUserDetails>()
        every { userDetails.orgId } returns orgId
        every { roleService.create(any()) } returns roleDto

        // When
        val response = roleController.create(roleDto)

        // Then
        assertEquals(HttpStatus.CREATED, response.statusCode)
        assertNotNull(response.body)
        assertEquals(1, response.body?.orgId)
        assertEquals("Test Role", response.body?.name)
        assertEquals(listOf("read", "write"), response.body?.accessRight)
        
        verify(exactly = 1) { roleService.create(roleDto) }
    }

    @Test
    fun `should find all roles with pagination`() {
        // Given
        val page = 0
        val size = 10
        val pageable = PageRequest.of(page, size)
        val orgId = 1
        val name: String? = null
        
        val roleDto1 = RoleDto(orgId = orgId, name = "Role 1", accessRight = listOf("read"))
        val roleDto2 = RoleDto(orgId = orgId, name = "Role 2", accessRight = listOf("write"))
        val roles = listOf(roleDto1, roleDto2)
        
        val pageResult = PageImpl(roles, pageable, roles.size.toLong())
        
        val userDetails = mockk<CustomUserDetails>()
        every { userDetails.orgId } returns orgId
        every { roleService.findAll(orgId, name, pageable) } returns pageResult

        // When
        val response = roleController.findAll(page, size, name, userDetails)

        // Then
        assertEquals(HttpStatus.OK, response.statusCode)
        assertNotNull(response.body)
        assertEquals(2, response.body?.content?.size)
        assertEquals(2, response.body?.totalElements)
        assertEquals("Role 1", response.body?.content?.get(0)?.name)
        assertEquals("Role 2", response.body?.content?.get(1)?.name)
        
        verify(exactly = 1) { roleService.findAll(orgId, name, pageable) }
    }
    
    @Test
    fun `should find roles with name filter`() {
        // Given
        val page = 0
        val size = 10
        val pageable = PageRequest.of(page, size)
        val orgId = 1
        val name = "Admin"
        
        val roleDto = RoleDto(orgId = orgId, name = "Admin Role", accessRight = listOf("read", "write"))
        val roles = listOf(roleDto)
        
        val pageResult = PageImpl(roles, pageable, roles.size.toLong())
        
        val userDetails = mockk<CustomUserDetails>()
        every { userDetails.orgId } returns orgId
        every { roleService.findAll(orgId, name, pageable) } returns pageResult

        // When
        val response = roleController.findAll(page, size, name, userDetails)

        // Then
        assertEquals(HttpStatus.OK, response.statusCode)
        assertNotNull(response.body)
        assertEquals(1, response.body?.content?.size)
        assertEquals(1, response.body?.totalElements)
        assertEquals("Admin Role", response.body?.content?.get(0)?.name)
        
        verify(exactly = 1) { roleService.findAll(orgId, name, pageable) }
    }

    @Test
    fun `should find role by id when role exists`() {
        // Given
        val orgId = 1
        val roleName = "test-role"
        val roleDto = RoleDto(
            orgId = orgId,
            name = roleName,
            accessRight = listOf("read", "write")
        )
        
        val userDetails = mockk<CustomUserDetails>()
        every { userDetails.orgId } returns orgId
        every { roleService.findById(orgId, roleName) } returns roleDto

        // When
        val response = roleController.findById(orgId, roleName, userDetails)

        // Then
        assertEquals(HttpStatus.OK, response.statusCode)
        assertNotNull(response.body)
        assertEquals(orgId, response.body?.orgId)
        assertEquals(roleName, response.body?.name)
        assertEquals(listOf("read", "write"), response.body?.accessRight)
        
        verify(exactly = 1) { roleService.findById(orgId, roleName) }
    }

    @Test
    fun `should return not found when role does not exist`() {
        // Given
        val orgId = 1
        val roleName = "non-existent-role"
        
        val userDetails = mockk<CustomUserDetails>()
        every { userDetails.orgId } returns orgId
        every { roleService.findById(orgId, roleName) } returns null

        // When
        val response = roleController.findById(orgId, roleName, userDetails)

        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
        assertNull(response.body)
        
        verify(exactly = 1) { roleService.findById(orgId, roleName) }
    }
    
    @Test
    fun `should return not found when role exists but has different orgId`() {
        // Given
        val orgId = 1
        val differentOrgId = 2
        val roleName = "test-role"
        val roleDto = RoleDto(
            orgId = differentOrgId,
            name = roleName,
            accessRight = listOf("read", "write")
        )
        
        val userDetails = mockk<CustomUserDetails>()
        every { userDetails.orgId } returns orgId
        every { roleService.findById(orgId, roleName) } returns roleDto

        // When
        val response = roleController.findById(orgId, roleName, userDetails)

        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
        assertNull(response.body)
        
        verify(exactly = 1) { roleService.findById(orgId, roleName) }
    }

    @Test
    fun `should update role successfully`() {
        // Given
        val orgId = 1
        val roleName = "test-role"
        val roleDto = RoleDto(
            orgId = orgId,
            name = roleName,
            accessRight = listOf("read", "write", "delete")
        )
        
        every { roleService.update(orgId, roleName, roleDto) } returns roleDto

        // When
        val response = roleController.update(orgId, roleName, roleDto)

        // Then
        assertEquals(HttpStatus.OK, response.statusCode)
        assertNotNull(response.body)
        assertEquals(orgId, response.body?.orgId)
        assertEquals(roleName, response.body?.name)
        assertEquals(listOf("read", "write", "delete"), response.body?.accessRight)
        
        verify(exactly = 1) { roleService.update(orgId, roleName, roleDto) }
    }

    @Test
    fun `should delete role successfully`() {
        // Given
        val orgId = 1
        val roleName = "test-role"
        val roleDto = RoleDto(
            orgId = orgId,
            name = roleName,
            accessRight = listOf("read", "write")
        )
        
        val userDetails = mockk<CustomUserDetails>()
        every { userDetails.orgId } returns orgId
        every { roleService.findById(orgId, roleName) } returns roleDto
        every { roleService.delete(orgId, roleName) } just runs

        // When
        val response = roleController.delete(orgId, roleName, userDetails)

        // Then
        assertEquals(HttpStatus.NO_CONTENT, response.statusCode)
        assertNull(response.body)
        
        verify(exactly = 1) { roleService.findById(orgId, roleName) }
        verify(exactly = 1) { roleService.delete(orgId, roleName) }
    }
    
    @Test
    fun `should return not found when deleting non-existent role`() {
        // Given
        val orgId = 1
        val roleName = "non-existent-role"
        
        val userDetails = mockk<CustomUserDetails>()
        every { userDetails.orgId } returns orgId
        every { roleService.findById(orgId, roleName) } returns null

        // When
        val response = roleController.delete(orgId, roleName, userDetails)

        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
        assertNull(response.body)
        
        verify(exactly = 1) { roleService.findById(orgId, roleName) }
        verify(exactly = 0) { roleService.delete(any(), any()) }
    }
    
    @Test
    fun `should return not found when deleting role with different orgId`() {
        // Given
        val orgId = 1
        val differentOrgId = 2
        val roleName = "test-role"
        val roleDto = RoleDto(
            orgId = differentOrgId,
            name = roleName,
            accessRight = listOf("read", "write")
        )
        
        val userDetails = mockk<CustomUserDetails>()
        every { userDetails.orgId } returns orgId
        every { roleService.findById(orgId, roleName) } returns roleDto

        // When
        val response = roleController.delete(orgId, roleName, userDetails)

        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
        assertNull(response.body)
        
        verify(exactly = 1) { roleService.findById(orgId, roleName) }
        verify(exactly = 0) { roleService.delete(any(), any()) }
    }
}