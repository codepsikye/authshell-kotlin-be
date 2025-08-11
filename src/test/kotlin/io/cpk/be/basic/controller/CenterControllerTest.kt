package io.cpk.be.basic.controller

import io.cpk.be.basic.dto.CenterDto
import io.cpk.be.basic.service.CenterService
import io.cpk.be.security.CustomUserDetails
import io.mockk.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.http.HttpStatus
import org.springframework.security.core.Authentication
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class CenterControllerTest {

    private lateinit var centerService: CenterService
    private lateinit var centerController: CenterController

    @BeforeEach
    fun setUp() {
        centerService = mockk()
        centerController = CenterController(centerService)
    }

    @Test
    fun `should create center successfully`() {
        // Given
        val centerDto = CenterDto(
            id = null,
            name = "Test Center",
            address = "Test Address",
            phone = "123-456-7890",
            orgId = 1
        )
        
        val createdCenterDto = centerDto.copy(id = 1)
        
        every { centerService.create(any()) } returns createdCenterDto

        // When
        val response = centerController.create(centerDto)

        // Then
        assertEquals(HttpStatus.CREATED, response.statusCode)
        assertNotNull(response.body)
        assertEquals(1, response.body?.id)
        assertEquals("Test Center", response.body?.name)
        assertEquals("Test Address", response.body?.address)
        assertEquals("123-456-7890", response.body?.phone)
        assertEquals(1, response.body?.orgId)
        
        verify(exactly = 1) { centerService.create(centerDto) }
    }

    @Test
    fun `should find all centers with pagination`() {
        // Given
        val page = 0
        val size = 10
        val pageable = PageRequest.of(page, size)
        val orgId = 1
        val name: String? = null
        
        val centerDto1 = CenterDto(id = 1, name = "Center 1", address = "Address 1", phone = "123-456-7890", orgId = orgId)
        val centerDto2 = CenterDto(id = 2, name = "Center 2", address = "Address 2", phone = "098-765-4321", orgId = orgId)
        val centers = listOf(centerDto1, centerDto2)
        
        val pageResult = PageImpl(centers, pageable, centers.size.toLong())
        
        val userDetails = mockk<CustomUserDetails>()
        every { userDetails.orgId } returns orgId
        every { centerService.findAll(orgId, name, pageable) } returns pageResult

        // When
        val response = centerController.findAll(page, size, name, userDetails)

        // Then
        assertEquals(HttpStatus.OK, response.statusCode)
        assertNotNull(response.body)
        assertEquals(2, response.body?.content?.size)
        assertEquals(2, response.body?.totalElements)
        
        verify(exactly = 1) { centerService.findAll(orgId, name, pageable) }
    }
    
    @Test
    fun `should find centers with name filter`() {
        // Given
        val page = 0
        val size = 10
        val pageable = PageRequest.of(page, size)
        val orgId = 1
        val name = "Medical"
        
        val centerDto = CenterDto(id = 1, name = "Medical Center", address = "Medical Address", phone = "123-456-7890", orgId = orgId)
        val centers = listOf(centerDto)
        
        val pageResult = PageImpl(centers, pageable, centers.size.toLong())
        
        val userDetails = mockk<CustomUserDetails>()
        every { userDetails.orgId } returns orgId
        every { centerService.findAll(orgId, name, pageable) } returns pageResult

        // When
        val response = centerController.findAll(page, size, name, userDetails)

        // Then
        assertEquals(HttpStatus.OK, response.statusCode)
        assertNotNull(response.body)
        assertEquals(1, response.body?.content?.size)
        assertEquals(1, response.body?.totalElements)
        assertEquals("Medical Center", response.body?.content?.get(0)?.name)
        
        verify(exactly = 1) { centerService.findAll(orgId, name, pageable) }
    }

    @Test
    fun `should find center by id when center exists`() {
        // Given
        val centerId = 1
        val orgId = 1
        val centerDto = CenterDto(
            id = centerId,
            name = "Test Center",
            address = "Test Address",
            phone = "123-456-7890",
            orgId = orgId
        )
        
        val userDetails = mockk<CustomUserDetails>()
        every { userDetails.orgId } returns orgId
        every { centerService.findById(centerId) } returns centerDto

        // When
        val response = centerController.findById(centerId, userDetails)

        // Then
        assertEquals(HttpStatus.OK, response.statusCode)
        assertNotNull(response.body)
        assertEquals(centerId, response.body?.id)
        assertEquals("Test Center", response.body?.name)
        assertEquals("Test Address", response.body?.address)
        
        verify(exactly = 1) { centerService.findById(centerId) }
    }

    @Test
    fun `should return not found when center does not exist`() {
        // Given
        val centerId = 999
        val orgId = 1
        
        val userDetails = mockk<CustomUserDetails>()
        every { userDetails.orgId } returns orgId
        every { centerService.findById(centerId) } returns null

        // When
        val response = centerController.findById(centerId, userDetails)

        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
        assertNull(response.body)
        
        verify(exactly = 1) { centerService.findById(centerId) }
    }
    
    @Test
    fun `should return not found when center exists but has different orgId`() {
        // Given
        val centerId = 1
        val userOrgId = 1
        val centerOrgId = 2
        
        val centerDto = CenterDto(
            id = centerId,
            name = "Test Center",
            address = "Test Address",
            phone = "123-456-7890",
            orgId = centerOrgId
        )
        
        val userDetails = mockk<CustomUserDetails>()
        every { userDetails.orgId } returns userOrgId
        every { centerService.findById(centerId) } returns centerDto

        // When
        val response = centerController.findById(centerId, userDetails)

        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
        assertNull(response.body)
        
        verify(exactly = 1) { centerService.findById(centerId) }
    }

    @Test
    fun `should update center successfully`() {
        // Given
        val centerId = 1
        val centerDto = CenterDto(
            id = centerId,
            name = "Updated Center",
            address = "Updated Address",
            phone = "987-654-3210",
            orgId = 1
        )
        
        every { centerService.update(centerId, centerDto) } returns centerDto

        // When
        val response = centerController.update(centerId, centerDto)

        // Then
        assertEquals(HttpStatus.OK, response.statusCode)
        assertNotNull(response.body)
        assertEquals(centerId, response.body?.id)
        assertEquals("Updated Center", response.body?.name)
        assertEquals("Updated Address", response.body?.address)
        assertEquals("987-654-3210", response.body?.phone)
        
        verify(exactly = 1) { centerService.update(centerId, centerDto) }
    }

    @Test
    fun `should delete center successfully`() {
        // Given
        val centerId = 1
        val orgId = 1
        val centerDto = CenterDto(
            id = centerId,
            name = "Test Center",
            address = "Test Address",
            phone = "123-456-7890",
            orgId = orgId
        )
        
        val userDetails = mockk<CustomUserDetails>()
        every { userDetails.orgId } returns orgId
        every { centerService.findById(centerId) } returns centerDto
        every { centerService.delete(centerId) } just runs

        // When
        val response = centerController.delete(centerId, userDetails)

        // Then
        assertEquals(HttpStatus.NO_CONTENT, response.statusCode)
        assertNull(response.body)
        
        verify(exactly = 1) { centerService.findById(centerId) }
        verify(exactly = 1) { centerService.delete(centerId) }
    }
    
    @Test
    fun `should return not found when deleting non-existent center`() {
        // Given
        val centerId = 999
        val orgId = 1
        
        val userDetails = mockk<CustomUserDetails>()
        every { userDetails.orgId } returns orgId
        every { centerService.findById(centerId) } returns null

        // When
        val response = centerController.delete(centerId, userDetails)

        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
        assertNull(response.body)
        
        verify(exactly = 1) { centerService.findById(centerId) }
        verify(exactly = 0) { centerService.delete(any()) }
    }
    
    @Test
    fun `should return not found when deleting center with different orgId`() {
        // Given
        val centerId = 1
        val userOrgId = 1
        val centerOrgId = 2
        
        val centerDto = CenterDto(
            id = centerId,
            name = "Test Center",
            address = "Test Address",
            phone = "123-456-7890",
            orgId = centerOrgId
        )
        
        val userDetails = mockk<CustomUserDetails>()
        every { userDetails.orgId } returns userOrgId
        every { centerService.findById(centerId) } returns centerDto

        // When
        val response = centerController.delete(centerId, userDetails)

        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
        assertNull(response.body)
        
        verify(exactly = 1) { centerService.findById(centerId) }
        verify(exactly = 0) { centerService.delete(any()) }
    }

    @Test
    fun `should get my centers successfully`() {
        // Given
        val userId = 123
        val centerDto1 = CenterDto(id = 1, name = "Center 1", address = "Address 1", phone = "123-456-7890", orgId = 1)
        val centerDto2 = CenterDto(id = 2, name = "Center 2", address = "Address 2", phone = "098-765-4321", orgId = 1)
        val centers = listOf(centerDto1, centerDto2)
        
        val authentication = mockk<Authentication>()
        val userDetails = mockk<CustomUserDetails>()
        
        every { authentication.principal } returns userDetails
        every { userDetails.id } returns userId
        every { centerService.findByUserId(userId) } returns centers

        // When
        val response = centerController.getMyCenters(authentication)

        // Then
        assertEquals(HttpStatus.OK, response.statusCode)
        assertNotNull(response.body)
        assertEquals(2, response.body?.size)
        assertEquals(1, response.body?.get(0)?.id)
        assertEquals("Center 1", response.body?.get(0)?.name)
        assertEquals("Address 1", response.body?.get(0)?.address)
        assertEquals(2, response.body?.get(1)?.id)
        assertEquals("Center 2", response.body?.get(1)?.name)
        assertEquals("Address 2", response.body?.get(1)?.address)
        
        verify(exactly = 1) { authentication.principal }
        verify(exactly = 1) { userDetails.id }
        verify(exactly = 1) { centerService.findByUserId(userId) }
    }

    @Test
    fun `should throw exception when user id is null`() {
        // Given
        val authentication = mockk<Authentication>()
        val userDetails = mockk<CustomUserDetails>()
        
        every { authentication.principal } returns userDetails
        every { userDetails.id } returns null

        // When & Then
        val exception = org.junit.jupiter.api.assertThrows<IllegalStateException> {
            centerController.getMyCenters(authentication)
        }
        assertEquals("User ID not found", exception.message)
        
        verify(exactly = 1) { authentication.principal }
        verify(exactly = 1) { userDetails.id }
        verify(exactly = 0) { centerService.findByUserId(any()) }
    }
}