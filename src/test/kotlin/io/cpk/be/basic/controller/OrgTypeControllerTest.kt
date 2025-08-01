package io.cpk.be.basic.controller

import io.cpk.be.basic.dto.OrgConfig
import io.cpk.be.basic.dto.OrgTypeDto
import io.cpk.be.basic.service.OrgTypeService
import io.mockk.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.http.HttpStatus
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class OrgTypeControllerTest {

    private lateinit var orgTypeService: OrgTypeService
    private lateinit var orgTypeController: OrgTypeController

    @BeforeEach
    fun setUp() {
        orgTypeService = mockk()
        orgTypeController = OrgTypeController(orgTypeService)
    }

    @Test
    fun `should create org type successfully`() {
        // Given
        val config = OrgConfig(mapOf("key1" to "value1", "key2" to "value2"))
        val orgTypeDto = OrgTypeDto(
            name = "Test Org Type",
            accessRight = listOf("read", "write"),
            orgConfigs = config
        )
        
        every { orgTypeService.create(any()) } returns orgTypeDto

        // When
        val response = orgTypeController.create(orgTypeDto)

        // Then
        assertEquals(HttpStatus.CREATED, response.statusCode)
        assertNotNull(response.body)
        assertEquals("Test Org Type", response.body?.name)
        assertEquals(listOf("read", "write"), response.body?.accessRight)
        assertEquals(config.toMap(), response.body?.orgConfigs?.toMap())
        
        verify(exactly = 1) { orgTypeService.create(orgTypeDto) }
    }

    @Test
    fun `should find all org types with pagination`() {
        // Given
        val page = 0
        val size = 10
        val pageable = PageRequest.of(page, size)
        val name: String? = null
        
        val config1 = OrgConfig(mapOf("key1" to "value1"))
        val config2 = OrgConfig(mapOf("key2" to "value2"))
        val orgTypeDto1 = OrgTypeDto(name = "Org Type 1", accessRight = listOf("read"), orgConfigs = config1)
        val orgTypeDto2 = OrgTypeDto(name = "Org Type 2", accessRight = listOf("write"), orgConfigs = config2)
        val orgTypes = listOf(orgTypeDto1, orgTypeDto2)
        
        val pageResult = PageImpl(orgTypes, pageable, orgTypes.size.toLong())
        
        every { orgTypeService.findAll(name, pageable) } returns pageResult

        // When
        val response = orgTypeController.findAll(page, size, name)

        // Then
        assertEquals(HttpStatus.OK, response.statusCode)
        assertNotNull(response.body)
        assertEquals(2, response.body?.content?.size)
        assertEquals(2, response.body?.totalElements)
        assertEquals("Org Type 1", response.body?.content?.get(0)?.name)
        assertEquals("Org Type 2", response.body?.content?.get(1)?.name)

        verify(exactly = 1) { orgTypeService.findAll(name, pageable) }
    }

    @Test
    fun `should find org type by id when org type exists`() {
        // Given
        val orgTypeId = "test-org-type"
        val config = OrgConfig(mapOf("key1" to "value1", "key2" to "value2"))
        val orgTypeDto = OrgTypeDto(
            name = "Test Org Type",
            accessRight = listOf("read", "write"),
            orgConfigs = config
        )
        
        every { orgTypeService.findById(orgTypeId) } returns orgTypeDto

        // When
        val response = orgTypeController.findById(orgTypeId)

        // Then
        assertEquals(HttpStatus.OK, response.statusCode)
        assertNotNull(response.body)
        assertEquals("Test Org Type", response.body?.name)
        assertEquals(listOf("read", "write"), response.body?.accessRight)
        assertEquals(config.toMap(), response.body?.orgConfigs?.toMap())
        
        verify(exactly = 1) { orgTypeService.findById(orgTypeId) }
    }

    @Test
    fun `should return not found when org type does not exist`() {
        // Given
        val orgTypeId = "non-existent-org-type"
        
        every { orgTypeService.findById(orgTypeId) } returns null

        // When
        val response = orgTypeController.findById(orgTypeId)

        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
        assertNull(response.body)
        
        verify(exactly = 1) { orgTypeService.findById(orgTypeId) }
    }

    @Test
    fun `should update org type successfully`() {
        // Given
        val orgTypeId = "test-org-type"
        val config = OrgConfig(mapOf("key1" to "updated-value1", "key3" to "value3"))
        val orgTypeDto = OrgTypeDto(
            name = "Updated Org Type",
            accessRight = listOf("read", "write", "delete"),
            orgConfigs = config
        )
        
        every { orgTypeService.update(orgTypeId, orgTypeDto) } returns orgTypeDto

        // When
        val response = orgTypeController.update(orgTypeId, orgTypeDto)

        // Then
        assertEquals(HttpStatus.OK, response.statusCode)
        assertNotNull(response.body)
        assertEquals("Updated Org Type", response.body?.name)
        assertEquals(listOf("read", "write", "delete"), response.body?.accessRight)
        assertEquals(config.toMap(), response.body?.orgConfigs?.toMap())
        
        verify(exactly = 1) { orgTypeService.update(orgTypeId, orgTypeDto) }
    }

    @Test
    fun `should delete org type successfully`() {
        // Given
        val orgTypeId = "test-org-type"
        
        every { orgTypeService.delete(orgTypeId) } just runs

        // When
        val response = orgTypeController.delete(orgTypeId)

        // Then
        assertEquals(HttpStatus.NO_CONTENT, response.statusCode)
        assertNull(response.body)
        
        verify(exactly = 1) { orgTypeService.delete(orgTypeId) }
    }
}