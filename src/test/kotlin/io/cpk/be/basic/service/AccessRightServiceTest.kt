package io.cpk.be.basic.service

import io.cpk.be.basic.dto.AccessRightDto
import io.cpk.be.basic.entity.AccessRight
import io.cpk.be.basic.mapper.AccessRightMapper
import io.cpk.be.basic.repository.AccessRightRepository
import io.mockk.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RestController
import java.io.File
import java.lang.reflect.Method
import java.net.URL
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertNull

/**
 * Unit tests for AccessRightService
 */
class AccessRightServiceTest {

    private val accessRightRepository = mockk<AccessRightRepository>()
    private val accessRightMapper = mockk<AccessRightMapper>()

    private lateinit var accessRightService: AccessRightService
    private lateinit var accessRight: AccessRight
    private lateinit var accessRightDto: AccessRightDto

    @BeforeEach
    fun setUp() {
        accessRightService = AccessRightService(accessRightRepository, accessRightMapper)

        accessRight = AccessRight(
            name = "TEST_RIGHT"
        )

        accessRightDto = AccessRightDto(
            name = "TEST_RIGHT"
        )
    }
    

    @Test
    fun `should create access right successfully`() {
        // Given
        val createDto = AccessRightDto(name = "NEW_RIGHT")
        val entityToSave = AccessRight(name = "NEW_RIGHT")
        val savedEntity = entityToSave

        every { accessRightMapper.toEntity(createDto) } returns entityToSave
        every { accessRightRepository.save(entityToSave) } returns savedEntity
        every { accessRightMapper.toDto(savedEntity) } returns createDto

        // When
        val result = accessRightService.create(createDto)

        // Then
        assertEquals(createDto, result)
        verify { accessRightMapper.toEntity(createDto) }
        verify { accessRightRepository.save(entityToSave) }
        verify { accessRightMapper.toDto(savedEntity) }
    }

    @Test
    fun `should find all access rights`() {
        // Given
        val accessRights = listOf(accessRight)
        val accessRightDtos = listOf(accessRightDto)

        every { accessRightRepository.findAll() } returns accessRights
        every { accessRightMapper.toDto(accessRight) } returns accessRightDto

        // When
        val result = accessRightService.findAll()

        // Then
        assertEquals(accessRightDtos, result)
        verify { accessRightRepository.findAll() }
        verify { accessRightMapper.toDto(accessRight) }
    }

    @Test
    fun `should find all access rights with pagination`() {
        // Given
        val pageable = PageRequest.of(0, 10)
        val accessRights = listOf(accessRight)
        val accessRightDtos = listOf(accessRightDto)
        val page = PageImpl(accessRights, pageable, accessRights.size.toLong())

        every { accessRightRepository.findAll(pageable) } returns page
        every { accessRightMapper.toDto(accessRight) } returns accessRightDto

        // When
        val result = accessRightService.findAll(pageable)

        // Then
        assertEquals(accessRightDtos, result.content)
        assertEquals(1, result.totalElements)
        verify { accessRightRepository.findAll(pageable) }
        verify { accessRightMapper.toDto(accessRight) }
    }

    @Test
    fun `should find all access rights by name with pagination`() {
        // Given
        val name = "TEST"
        val pageable = PageRequest.of(0, 10)
        val accessRights = listOf(accessRight)
        val accessRightDtos = listOf(accessRightDto)
        val page = PageImpl(accessRights, pageable, accessRights.size.toLong())

        every { accessRightRepository.findAllByNameContaining(name, pageable) } returns page
        every { accessRightMapper.toDto(accessRight) } returns accessRightDto

        // When
        val result = accessRightService.findAll(name, pageable)

        // Then
        assertEquals(accessRightDtos, result.content)
        assertEquals(1, result.totalElements)
        verify { accessRightRepository.findAllByNameContaining(name, pageable) }
        verify { accessRightMapper.toDto(accessRight) }
    }

    @Test
    fun `should handle empty name in findAll with name parameter`() {
        // Given
        val name: String? = null
        val pageable = PageRequest.of(0, 10)
        val accessRights = listOf(accessRight)
        val accessRightDtos = listOf(accessRightDto)
        val page = PageImpl(accessRights, pageable, accessRights.size.toLong())

        every { accessRightRepository.findAllByNameContaining("", pageable) } returns page
        every { accessRightMapper.toDto(accessRight) } returns accessRightDto

        // When
        val result = accessRightService.findAll(name, pageable)

        // Then
        assertEquals(accessRightDtos, result.content)
        assertEquals(1, result.totalElements)
        verify { accessRightRepository.findAllByNameContaining("", pageable) }
        verify { accessRightMapper.toDto(accessRight) }
    }

    @Test
    fun `should find access right by id successfully`() {
        // Given
        val name = "TEST_RIGHT"

        every { accessRightRepository.findById(name) } returns Optional.of(accessRight)
        every { accessRightMapper.toDto(accessRight) } returns accessRightDto

        // When
        val result = accessRightService.findById(name)

        // Then
        assertEquals(accessRightDto, result)
        verify { accessRightRepository.findById(name) }
        verify { accessRightMapper.toDto(accessRight) }
    }

    @Test
    fun `should return null when access right not found by id`() {
        // Given
        val name = "NONEXISTENT"

        every { accessRightRepository.findById(name) } returns Optional.empty()

        // When
        val result = accessRightService.findById(name)

        // Then
        assertNull(result)
        verify { accessRightRepository.findById(name) }
        verify(exactly = 0) { accessRightMapper.toDto(any()) }
    }

    @Test
    fun `should update access right successfully`() {
        // Given
        val name = "TEST_RIGHT"
        val updateDto = AccessRightDto(name = name)
        val existingEntity = accessRight
        val entityToUpdate = AccessRight(name = name)
        val updatedEntity = entityToUpdate
        val resultDto = updateDto

        every { accessRightRepository.findById(name) } returns Optional.of(existingEntity)
        every { accessRightMapper.toEntity(updateDto) } returns entityToUpdate
        every { accessRightRepository.save(any()) } returns updatedEntity
        every { accessRightMapper.toDto(updatedEntity) } returns resultDto

        // When
        val result = accessRightService.update(name, updateDto)

        // Then
        assertEquals(resultDto, result)
        verify { accessRightRepository.findById(name) }
        verify { accessRightRepository.save(any()) }
        verify { accessRightMapper.toDto(updatedEntity) }
    }

    @Test
    fun `should throw exception when updating non-existent access right`() {
        // Given
        val name = "NONEXISTENT"
        val updateDto = AccessRightDto(name = name)

        every { accessRightRepository.findById(name) } returns Optional.empty()

        // When & Then
        val exception = assertThrows<RuntimeException> {
            accessRightService.update(name, updateDto)
        }
        assertEquals("AccessRight not found", exception.message)
        verify { accessRightRepository.findById(name) }
        verify(exactly = 0) { accessRightRepository.save(any()) }
    }

    @Test
    fun `should delete access right successfully`() {
        // Given
        val name = "TEST_RIGHT"

        every { accessRightRepository.deleteById(name) } just runs

        // When
        accessRightService.delete(name)

        // Then
        verify { accessRightRepository.deleteById(name) }
    }
}