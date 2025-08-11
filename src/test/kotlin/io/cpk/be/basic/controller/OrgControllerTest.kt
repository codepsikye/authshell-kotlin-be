package io.cpk.be.basic.controller

import io.cpk.be.basic.dto.*
import io.cpk.be.basic.entity.AccessRight
import io.cpk.be.basic.repository.AccessRightRepository
import io.cpk.be.basic.service.*
import io.mockk.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.http.HttpStatus
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class OrgControllerTest {

    private val orgService = mockk<OrgService>()
    private val centerService = mockk<CenterService>()
    private val appUserService = mockk<AppUserService>()
    private val roleService = mockk<RoleService>()
    private val appUserRoleService = mockk<AppUserRoleService>()
    private val accessRightRepository = mockk<AccessRightRepository>()

    private lateinit var orgController: OrgController

    @BeforeEach
    fun setUp() {
        orgController = OrgController(
            orgService,
            centerService,
            appUserService,
            roleService,
            appUserRoleService,
            accessRightRepository
        )
    }

    @Test
    fun `should create org with center and user successfully`() {
        // Given
        val orgId = 1
        val centerId = 1
        val userId = 1
        
        // Create test DTOs
        val orgDto = OrgDto(
            name = "Test Org",
            address = "123 Test St",
            phone = "555-1234",
            city = "Test City",
            country = "Test Country",
            notes = "Created by test",
            orgTypeName = "org-admin"
        )
        
        val centerDto = CenterDto(
            name = "Test Center",
            address = "456 Test Ave",
            phone = "555-5678",
            orgId = 0 // This will be overridden by the controller
        )
        
        val userDto = AppUserDto(
            id = userId,
            orgId = 0, // This will be overridden by the controller
            username = "testuser",
            fullname = "Test User",
            title = "Test Title",
            email = "test@example.com",
            password = "password123",
            orgAdmin = true
        )
        
        val request = CreateOrgRequest(
            orgDto = orgDto,
            centerDto = centerDto,
            userDto = userDto
        )
        
        // Create expected return values
        val createdOrgDto = orgDto.copy(id = orgId)
        val createdCenterDto = centerDto.copy(id = centerId, orgId = orgId)
        val createdUserDto = userDto.copy(orgId = orgId)
        
        // Mock access rights
        val accessRights = listOf(
            AccessRight("org_create"),
            AccessRight("org_read"),
            AccessRight("center_create")
        )
        
        // Mock service method calls
        every { orgService.create(orgDto) } returns createdOrgDto
        every { centerService.create(centerDto.copy(orgId = orgId)) } returns createdCenterDto
        every { appUserService.create(userDto.copy(orgId = orgId)) } returns createdUserDto
        every { accessRightRepository.findAll() } returns accessRights
        every { roleService.findById(orgId, "admin") } returns null
        
        // Create the expected role DTO that will be passed to roleService.create
        val expectedRoleDto = RoleDto(
            orgId = orgId,
            name = "admin",
            accessRight = accessRights.map { it.name }
        )
        
        every { roleService.create(expectedRoleDto) } returns expectedRoleDto
        every { appUserRoleService.create(any()) } returns AppUserRoleDto(
            userId = userId,
            orgId = orgId,
            centerId = centerId,
            roleName = "admin"
        )
        
        // When
        val response = orgController.createOrgWithCenterAndUser(request)
        
        // Then
        assertEquals(HttpStatus.CREATED, response.statusCode)
        assertNotNull(response.body)
        assertEquals(orgId, response.body!!.id)
        
        // Verify service method calls
        verify { orgService.create(orgDto) }
        verify { centerService.create(centerDto.copy(orgId = orgId)) }
        verify { appUserService.create(userDto.copy(orgId = orgId)) }
        verify { accessRightRepository.findAll() }
        verify { roleService.findById(orgId, "admin") }
        verify { roleService.create(expectedRoleDto) }
        verify { 
            appUserRoleService.create(
                AppUserRoleDto(
                    userId = userId,
                    orgId = orgId,
                    centerId = centerId,
                    roleName = "admin"
                )
            ) 
        }
    }
    
    @Test
    fun `should create org with center and user when admin role already exists`() {
        // Given
        val orgId = 1
        val centerId = 1
        val userId = 1
        
        // Create test DTOs
        val orgDto = OrgDto(
            name = "Test Org",
            address = "123 Test St",
            phone = "555-1234",
            city = "Test City",
            country = "Test Country",
            notes = "Created by test",
            orgTypeName = "org-admin"
        )
        
        val centerDto = CenterDto(
            name = "Test Center",
            address = "456 Test Ave",
            phone = "555-5678",
            orgId = 0 // This will be overridden by the controller
        )
        
        val userDto = AppUserDto(
            id = userId,
            orgId = 0, // This will be overridden by the controller
            username = "testuser",
            fullname = "Test User",
            title = "Test Title",
            email = "test@example.com",
            password = "password123",
            orgAdmin = true
        )
        
        val request = CreateOrgRequest(
            orgDto = orgDto,
            centerDto = centerDto,
            userDto = userDto
        )
        
        // Create expected return values
        val createdOrgDto = orgDto.copy(id = orgId)
        val createdCenterDto = centerDto.copy(id = centerId, orgId = orgId)
        val createdUserDto = userDto.copy(orgId = orgId)
        
        // Mock access rights
        val accessRights = listOf(
            AccessRight("org_create"),
            AccessRight("org_read"),
            AccessRight("center_create")
        )
        
        // Create existing admin role
        val existingAdminRole = RoleDto(
            orgId = orgId,
            name = "admin",
            accessRight = accessRights.map { it.name }
        )
        
        // Mock service method calls
        every { orgService.create(orgDto) } returns createdOrgDto
        every { centerService.create(centerDto.copy(orgId = orgId)) } returns createdCenterDto
        every { appUserService.create(userDto.copy(orgId = orgId)) } returns createdUserDto
        every { accessRightRepository.findAll() } returns accessRights
        every { roleService.findById(orgId, "admin") } returns existingAdminRole
        every { appUserRoleService.create(any()) } returns AppUserRoleDto(
            userId = userId,
            orgId = orgId,
            centerId = centerId,
            roleName = "admin"
        )
        
        // When
        val response = orgController.createOrgWithCenterAndUser(request)
        
        // Then
        assertEquals(HttpStatus.CREATED, response.statusCode)
        assertNotNull(response.body)
        assertEquals(orgId, response.body!!.id)
        
        // Verify service method calls
        verify { orgService.create(orgDto) }
        verify { centerService.create(centerDto.copy(orgId = orgId)) }
        verify { appUserService.create(userDto.copy(orgId = orgId)) }
        verify { accessRightRepository.findAll() }
        verify { roleService.findById(orgId, "admin") }
        
        // Verify that roleService.create is never called
        verify(exactly = 0) { 
            roleService.create(
                RoleDto(
                    orgId = orgId,
                    name = "admin",
                    accessRight = accessRights.map { it.name }
                )
            ) 
        }
        verify { 
            appUserRoleService.create(
                AppUserRoleDto(
                    userId = userId,
                    orgId = orgId,
                    centerId = centerId,
                    roleName = "admin"
                )
            ) 
        }
    }
    
    @Test
    fun `should find all orgs with pagination`() {
        // Given
        val page = 0
        val size = 10
        val pageable = PageRequest.of(page, size)
        val name: String? = null
        
        val orgDto1 = OrgDto(id = 1, name = "Org 1", address = "Address 1", phone = "123-456-7890", orgTypeName = "type1")
        val orgDto2 = OrgDto(id = 2, name = "Org 2", address = "Address 2", phone = "098-765-4321", orgTypeName = "type2")
        val orgs = listOf(orgDto1, orgDto2)
        
        val pageResult = PageImpl(orgs, pageable, orgs.size.toLong())
        
        every { orgService.findAll(name, pageable) } returns pageResult

        // When
        val response = orgController.findAll(page, size, name)

        // Then
        assertEquals(HttpStatus.OK, response.statusCode)
        assertNotNull(response.body)
        assertEquals(2, response.body?.content?.size)
        assertEquals(2, response.body?.totalElements)
        assertEquals("Org 1", response.body?.content?.get(0)?.name)
        assertEquals("Org 2", response.body?.content?.get(1)?.name)
        
        verify(exactly = 1) { orgService.findAll(name, pageable) }
    }
    
    @Test
    fun `should find orgs with name filter`() {
        // Given
        val page = 0
        val size = 10
        val pageable = PageRequest.of(page, size)
        val name = "Test"
        
        val orgDto = OrgDto(id = 1, name = "Test Org", address = "Test Address", phone = "123-456-7890", orgTypeName = "test-type")
        val orgs = listOf(orgDto)
        
        val pageResult = PageImpl(orgs, pageable, orgs.size.toLong())
        
        every { orgService.findAll(name, pageable) } returns pageResult

        // When
        val response = orgController.findAll(page, size, name)

        // Then
        assertEquals(HttpStatus.OK, response.statusCode)
        assertNotNull(response.body)
        assertEquals(1, response.body?.content?.size)
        assertEquals(1, response.body?.totalElements)
        assertEquals("Test Org", response.body?.content?.get(0)?.name)
        
        verify(exactly = 1) { orgService.findAll(name, pageable) }
    }
    
    @Test
    fun `should find org by id when org exists`() {
        // Given
        val orgId = 1
        val orgDto = OrgDto(
            id = orgId,
            name = "Test Org",
            address = "Test Address",
            phone = "123-456-7890",
            orgTypeName = "test-type"
        )
        
        every { orgService.findById(orgId) } returns orgDto

        // When
        val response = orgController.findById(orgId)

        // Then
        assertEquals(HttpStatus.OK, response.statusCode)
        assertNotNull(response.body)
        assertEquals(orgId, response.body?.id)
        assertEquals("Test Org", response.body?.name)
        assertEquals("Test Address", response.body?.address)
        
        verify(exactly = 1) { orgService.findById(orgId) }
    }
    
    @Test
    fun `should return not found when org does not exist`() {
        // Given
        val orgId = 999
        
        every { orgService.findById(orgId) } returns null

        // When
        val response = orgController.findById(orgId)

        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
        assertNull(response.body)
        
        verify(exactly = 1) { orgService.findById(orgId) }
    }
    
    @Test
    fun `should update org successfully`() {
        // Given
        val orgId = 1
        val orgDto = OrgDto(
            id = orgId,
            name = "Updated Org",
            address = "Updated Address",
            phone = "987-654-3210",
            orgTypeName = "updated-type"
        )
        
        every { orgService.update(orgId, orgDto) } returns orgDto

        // When
        val response = orgController.update(orgId, orgDto)

        // Then
        assertEquals(HttpStatus.OK, response.statusCode)
        assertNotNull(response.body)
        assertEquals(orgId, response.body?.id)
        assertEquals("Updated Org", response.body?.name)
        assertEquals("Updated Address", response.body?.address)
        assertEquals("987-654-3210", response.body?.phone)
        
        verify(exactly = 1) { orgService.update(orgId, orgDto) }
    }
    
    @Test
    fun `should delete org successfully`() {
        // Given
        val orgId = 1
        
        every { orgService.delete(orgId) } just runs

        // When
        val response = orgController.delete(orgId)

        // Then
        assertEquals(HttpStatus.NO_CONTENT, response.statusCode)
        assertNull(response.body)
        
        verify(exactly = 1) { orgService.delete(orgId) }
    }
    @Test
    fun `should handle error when creating org fails`() {
        // Given
        val orgDto = OrgDto(
            name = "Test Org",
            address = "123 Test St",
            phone = "555-1234",
            city = "Test City",
            country = "Test Country",
            notes = "Created by test",
            orgTypeName = "org-admin"
        )
        
        val centerDto = CenterDto(
            name = "Test Center",
            address = "456 Test Ave",
            phone = "555-5678",
            orgId = 0
        )
        
        val userDto = AppUserDto(
            id = 1,
            orgId = 0,
            username = "testuser",
            fullname = "Test User",
            title = "Test Title",
            email = "test@example.com",
            password = "password123",
            orgAdmin = true
        )
        
        val request = CreateOrgRequest(
            orgDto = orgDto,
            centerDto = centerDto,
            userDto = userDto
        )
        
        // Mock service to throw exception
        every { orgService.create(orgDto) } throws RuntimeException("Database error")
        
        // When/Then
        assertThrows<RuntimeException> {
            orgController.createOrgWithCenterAndUser(request)
        }
        
        // Verify service method calls
        verify { orgService.create(orgDto) }
        verify(exactly = 0) { centerService.create(any()) }
        verify(exactly = 0) { appUserService.create(any()) }
    }
}