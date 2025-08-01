package io.cpk.be.basic.service

import io.cpk.be.basic.dto.OrgConfig
import io.cpk.be.basic.dto.OrgTypeDto
import io.cpk.be.basic.entity.OrgType
import io.cpk.be.basic.mapper.OrgTypeMapper
import io.cpk.be.basic.repository.OrgTypeRepository
import io.mockk.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import java.time.LocalDateTime
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertNull

/**
 * Unit tests for OrgTypeService
 */
class OrgTypeServiceTest {

    private val orgTypeRepository = mockk<OrgTypeRepository>()
    private val orgTypeMapper = mockk<OrgTypeMapper>()

    private lateinit var orgTypeService: OrgTypeService
    private lateinit var orgType: OrgType
    private lateinit var orgTypeDto: OrgTypeDto

    @BeforeEach
    fun setUp() {
        orgTypeService = OrgTypeService(orgTypeRepository, orgTypeMapper)

        val now = LocalDateTime.now()
        val config = OrgConfig(mapOf("key1" to "value1", "key2" to 123))
        
        orgType = OrgType(
            name = "TEST_TYPE",
            accessRight = listOf("READ", "WRITE"),
            orgConfigs = config
        )

        orgTypeDto = OrgTypeDto(
            name = "TEST_TYPE",
            accessRight = listOf("READ", "WRITE"),
            orgConfigs = config
        )
    }

    @Test
    fun `should create org type successfully`() {
        // Given
        val config = OrgConfig(mapOf("key1" to "value1"))
        val createDto = OrgTypeDto(
            name = "NEW_TYPE",
            accessRight = listOf("READ"),
            orgConfigs = config
        )
        val entityToSave = OrgType(
            name = "NEW_TYPE",
            accessRight = listOf("READ"),
            orgConfigs = config
        )
        val savedEntity = entityToSave

        every { orgTypeMapper.toEntity(createDto) } returns entityToSave
        every { orgTypeRepository.save(entityToSave) } returns savedEntity
        every { orgTypeMapper.toDto(savedEntity) } returns createDto

        // When
        val result = orgTypeService.create(createDto)

        // Then
        assertEquals(createDto, result)
        verify { orgTypeMapper.toEntity(createDto) }
        verify { orgTypeRepository.save(entityToSave) }
        verify { orgTypeMapper.toDto(savedEntity) }
    }

    @Test
    fun `should find all org types`() {
        // Given
        val orgTypes = listOf(orgType)
        val orgTypeDtos = listOf(orgTypeDto)

        every { orgTypeRepository.findAll() } returns orgTypes
        every { orgTypeMapper.toDto(orgType) } returns orgTypeDto

        // When
        val result = orgTypeService.findAll()

        // Then
        assertEquals(orgTypeDtos, result)
        verify { orgTypeRepository.findAll() }
        verify { orgTypeMapper.toDto(orgType) }
    }

    @Test
    fun `should find all org types with pagination`() {
        // Given
        val pageable = PageRequest.of(0, 10)
        val orgTypes = listOf(orgType)
        val orgTypeDtos = listOf(orgTypeDto)
        val page = PageImpl(orgTypes, pageable, orgTypes.size.toLong())

        every { orgTypeRepository.findAll(pageable) } returns page
        every { orgTypeMapper.toDto(orgType) } returns orgTypeDto

        // When
        val result = orgTypeService.findAll(pageable)

        // Then
        assertEquals(orgTypeDtos, result.content)
        assertEquals(page.totalElements, result.totalElements)
        verify { orgTypeRepository.findAll(pageable) }
        verify { orgTypeMapper.toDto(orgType) }
    }

    @Test
    fun `should find all org types by name with pagination`() {
        // Given
        val name = "TEST"
        val pageable = PageRequest.of(0, 10)
        val orgTypes = listOf(orgType)
        val orgTypeDtos = listOf(orgTypeDto)
        val page = PageImpl(orgTypes, pageable, orgTypes.size.toLong())

        every { orgTypeRepository.findAllByNameContaining(name, pageable) } returns page
        every { orgTypeMapper.toDto(orgType) } returns orgTypeDto

        // When
        val result = orgTypeService.findAll(name, pageable)

        // Then
        assertEquals(orgTypeDtos, result.content)
        assertEquals(page.totalElements, result.totalElements)
        verify { orgTypeRepository.findAllByNameContaining(name, pageable) }
        verify { orgTypeMapper.toDto(orgType) }
    }

    @Test
    fun `should find org type by id when exists`() {
        // Given
        val id = "TEST_TYPE"

        every { orgTypeRepository.findById(id) } returns Optional.of(orgType)
        every { orgTypeMapper.toDto(orgType) } returns orgTypeDto

        // When
        val result = orgTypeService.findById(id)

        // Then
        assertEquals(orgTypeDto, result)
        verify { orgTypeRepository.findById(id) }
        verify { orgTypeMapper.toDto(orgType) }
    }

    @Test
    fun `should return null when org type not found by id`() {
        // Given
        val id = "NON_EXISTENT"

        every { orgTypeRepository.findById(id) } returns Optional.empty()

        // When
        val result = orgTypeService.findById(id)

        // Then
        assertNull(result)
        verify { orgTypeRepository.findById(id) }
        verify(exactly = 0) { orgTypeMapper.toDto(any()) }
    }

    @Test
    fun `should update org type when exists`() {
        // Given
        val id = "TEST_TYPE"
        val config = OrgConfig(mapOf("key1" to "updated", "key3" to true))
        val updateDto = OrgTypeDto(
            name = "TEST_TYPE",
            accessRight = listOf("READ", "WRITE", "DELETE"),
            orgConfigs = config
        )
        val existingEntity = orgType
        val updatedEntity = OrgType(
            name = "TEST_TYPE",
            accessRight = listOf("READ", "WRITE", "DELETE"),
            orgConfigs = config
        )

        every { orgTypeRepository.findById(id) } returns Optional.of(existingEntity)
        every { orgTypeMapper.toEntity(updateDto) } returns updatedEntity
        every { orgTypeRepository.save(updatedEntity) } returns updatedEntity
        every { orgTypeMapper.toDto(updatedEntity) } returns updateDto

        // When
        val result = orgTypeService.update(id, updateDto)

        // Then
        assertEquals(updateDto, result)
        verify { orgTypeRepository.findById(id) }
        verify { orgTypeMapper.toEntity(updateDto) }
        verify { orgTypeRepository.save(updatedEntity) }
        verify { orgTypeMapper.toDto(updatedEntity) }
    }

    @Test
    fun `should throw exception when updating non-existent org type`() {
        // Given
        val id = "NON_EXISTENT"
        val config = OrgConfig(mapOf("key1" to "value1"))
        val updateDto = OrgTypeDto(
            name = "NON_EXISTENT",
            accessRight = listOf("READ"),
            orgConfigs = config
        )

        every { orgTypeRepository.findById(id) } returns Optional.empty()

        // When/Then
        assertThrows<NoSuchElementException> {
            orgTypeService.update(id, updateDto)
        }

        verify { orgTypeRepository.findById(id) }
        verify(exactly = 0) { orgTypeMapper.toEntity(any()) }
        verify(exactly = 0) { orgTypeRepository.save(any()) }
        verify(exactly = 0) { orgTypeMapper.toDto(any()) }
    }

    @Test
    fun `should delete org type when exists`() {
        // Given
        val id = "TEST_TYPE"

        every { orgTypeRepository.existsById(id) } returns true
        every { orgTypeRepository.deleteById(id) } just runs

        // When
        orgTypeService.delete(id)

        // Then
        verify { orgTypeRepository.existsById(id) }
        verify { orgTypeRepository.deleteById(id) }
    }

    @Test
    fun `should throw exception when deleting non-existent org type`() {
        // Given
        val id = "NON_EXISTENT"

        every { orgTypeRepository.existsById(id) } returns false

        // When/Then
        assertThrows<NoSuchElementException> {
            orgTypeService.delete(id)
        }

        verify { orgTypeRepository.existsById(id) }
        verify(exactly = 0) { orgTypeRepository.deleteById(any()) }
    }

    @Test
    fun `should handle complex org configs`() {
        // Given
        val config = OrgConfig(mapOf(
            "stringValue" to "text",
            "intValue" to 42,
            "boolValue" to true,
            "listValue" to listOf(1, 2, 3),
            "mapValue" to mapOf("nested" to "value")
        ))
        
        val complexOrgType = OrgType(
            name = "COMPLEX_TYPE",
            accessRight = listOf("READ", "WRITE"),
            orgConfigs = config
        )
        
        val complexOrgTypeDto = OrgTypeDto(
            name = "COMPLEX_TYPE",
            accessRight = listOf("READ", "WRITE"),
            orgConfigs = config
        )

        every { orgTypeRepository.findById("COMPLEX_TYPE") } returns Optional.of(complexOrgType)
        every { orgTypeMapper.toDto(complexOrgType) } returns complexOrgTypeDto

        // When
        val result = orgTypeService.findById("COMPLEX_TYPE")

        // Then
        assertEquals(complexOrgTypeDto, result)
        verify { orgTypeRepository.findById("COMPLEX_TYPE") }
        verify { orgTypeMapper.toDto(complexOrgType) }
    }
}