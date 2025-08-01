package io.cpk.be.basic.controller

import io.cpk.be.basic.dto.AccessRightDto
import io.cpk.be.basic.service.AccessRightService
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
 * Unit tests for AccessRightController
 */
class AccessRightControllerTest {

    private lateinit var accessRightService: AccessRightService
    private lateinit var accessRightController: AccessRightController

    @BeforeEach
    fun setUp() {
        accessRightService = mockk()
        accessRightController = AccessRightController(accessRightService)
    }

    @Test
    fun `should create access right successfully`() {
        // Given
        val accessRightDto = AccessRightDto(
            name = "test_access_right"
        )
        
        val createdAccessRightDto = AccessRightDto(name = "test_access_right")
        
        every { accessRightService.create(any()) } returns createdAccessRightDto

        // When
        val response = accessRightController.create(accessRightDto)

        // Then
        assertEquals(HttpStatus.CREATED, response.statusCode)
        assertNotNull(response.body)
        assertEquals("test_access_right", response.body?.name)
        
        verify(exactly = 1) { accessRightService.create(accessRightDto) }
    }

    @Test
    fun `should find all access rights with pagination`() {
        // Given
        val page = 0
        val size = 10
        val pageable = PageRequest.of(page, size)
        val name: String? = null
        
        val accessRightDto1 = AccessRightDto(name = "right_one")
        val accessRightDto2 = AccessRightDto(name = "right_two")
        val accessRights = listOf(accessRightDto1, accessRightDto2)
        
        val pageResult = PageImpl(accessRights, pageable, accessRights.size.toLong())
        
        every { accessRightService.findAll(name, pageable) } returns pageResult

        // When
        val response = accessRightController.findAll(page, size, name)

        // Then
        assertEquals(HttpStatus.OK, response.statusCode)
        assertNotNull(response.body)
        assertEquals(2, response.body?.content?.size)
        assertEquals(2, response.body?.totalElements)
        
        verify(exactly = 1) { accessRightService.findAll(name, pageable) }
    }

    @Test
    fun `should find access right by id when it exists`() {
        // Given
        val accessRightName = "test_right"
        val accessRightDto = AccessRightDto(
            name = accessRightName
        )
        
        every { accessRightService.findById(accessRightName) } returns accessRightDto

        // When
        val response = accessRightController.findById(accessRightName)

        // Then
        assertEquals(HttpStatus.OK, response.statusCode)
        assertNotNull(response.body)
        assertEquals(accessRightName, response.body?.name)
        
        verify(exactly = 1) { accessRightService.findById(accessRightName) }
    }

    @Test
    fun `should return not found when access right does not exist`() {
        // Given
        val accessRightName = "nonexistent"
        
        every { accessRightService.findById(accessRightName) } returns null

        // When
        val response = accessRightController.findById(accessRightName)

        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
        assertNull(response.body)
        
        verify(exactly = 1) { accessRightService.findById(accessRightName) }
    }

    @Test
    fun `should update access right successfully`() {
        // Given
        val accessRightName = "test_right"
        val accessRightDto = AccessRightDto(
            name = "updated_right"
        )
        
        val updatedAccessRightDto = AccessRightDto(name = "updated_right")
        
        every { accessRightService.update(accessRightName, accessRightDto) } returns updatedAccessRightDto

        // When
        val response = accessRightController.update(accessRightName, accessRightDto)

        // Then
        assertEquals(HttpStatus.OK, response.statusCode)
        assertNotNull(response.body)
        assertEquals("updated_right", response.body?.name)
        
        verify(exactly = 1) { accessRightService.update(accessRightName, accessRightDto) }
    }

    @Test
    fun `should delete access right successfully`() {
        // Given
        val accessRightName = "test_right"
        
        every { accessRightService.delete(accessRightName) } just runs

        // When
        val response = accessRightController.delete(accessRightName)

        // Then
        assertEquals(HttpStatus.NO_CONTENT, response.statusCode)
        assertNull(response.body)
        
        verify(exactly = 1) { accessRightService.delete(accessRightName) }
    }
}