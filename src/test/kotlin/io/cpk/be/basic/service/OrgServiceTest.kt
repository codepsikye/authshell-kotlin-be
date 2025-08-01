package io.cpk.be.basic.service

import io.cpk.be.basic.dto.OrgDto
import io.cpk.be.basic.entity.Org
import io.cpk.be.basic.mapper.OrgMapper
import io.cpk.be.basic.repository.OrgRepository
import io.mockk.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertNull

/**
 * Unit tests for OrgService
 */
class OrgServiceTest {

    private val orgRepository = mockk<OrgRepository>()
    private val orgMapper = mockk<OrgMapper>()

    private lateinit var orgService: OrgService
    private lateinit var org: Org
    private lateinit var orgDto: OrgDto

    @BeforeEach
    fun setUp() {
        orgService = OrgService(orgRepository, orgMapper)
        
        org = Org(
            id = 1,
            name = "Test Organization",
            address = "123 Test St",
            phone = "123-456-7890",
            city = "Test City",
            country = "Test Country",
            notes = "Test Notes",
            orgTypeName = "TEST_TYPE"
        )

        orgDto = OrgDto(
            id = 1,
            name = "Test Organization",
            address = "123 Test St",
            phone = "123-456-7890",
            city = "Test City",
            country = "Test Country",
            notes = "Test Notes",
            orgTypeName = "TEST_TYPE"
        )
    }

    @Test
    fun `should create org successfully`() {
        // Given
        val createDto = OrgDto(
            name = "New Organization",
            orgTypeName = "TEST_TYPE"
        )
        val entityToSave = Org(
            name = "New Organization",
            orgTypeName = "TEST_TYPE"
        )
        val savedEntity = Org(
            id = 2,
            name = "New Organization",
            orgTypeName = "TEST_TYPE"
        )
        val resultDto = OrgDto(
            id = 2,
            name = "New Organization",
            orgTypeName = "TEST_TYPE"
        )

        every { orgMapper.toEntity(createDto) } returns entityToSave
        every { orgRepository.save(entityToSave) } returns savedEntity
        every { orgMapper.toDto(savedEntity) } returns resultDto

        // When
        val result = orgService.create(createDto)

        // Then
        assertEquals(resultDto, result)
        verify { orgMapper.toEntity(createDto) }
        verify { orgRepository.save(entityToSave) }
        verify { orgMapper.toDto(savedEntity) }
    }

    @Test
    fun `should find all orgs`() {
        // Given
        val orgs = listOf(org)
        val orgDtos = listOf(orgDto)

        every { orgRepository.findAll() } returns orgs
        every { orgMapper.toDto(org) } returns orgDto

        // When
        val result = orgService.findAll()

        // Then
        assertEquals(orgDtos, result)
        verify { orgRepository.findAll() }
        verify { orgMapper.toDto(org) }
    }

    @Test
    fun `should find all orgs with pagination`() {
        // Given
        val pageable = PageRequest.of(0, 10)
        val orgs = listOf(org)
        val orgDtos = listOf(orgDto)
        val page = PageImpl(orgs, pageable, orgs.size.toLong())

        every { orgRepository.findAll(pageable) } returns page
        every { orgMapper.toDto(org) } returns orgDto

        // When
        val result = orgService.findAll(pageable)

        // Then
        assertEquals(orgDtos, result.content)
        assertEquals(1, result.totalElements)
        verify { orgRepository.findAll(pageable) }
        verify { orgMapper.toDto(org) }
    }

    @Test
    fun `should find all orgs by name with pagination`() {
        // Given
        val name = "Test"
        val pageable = PageRequest.of(0, 10)
        val orgs = listOf(org)
        val orgDtos = listOf(orgDto)
        val page = PageImpl(orgs, pageable, orgs.size.toLong())

        every { orgRepository.findAllByNameContaining(name, pageable) } returns page
        every { orgMapper.toDto(org) } returns orgDto

        // When
        val result = orgService.findAll(name, pageable)

        // Then
        assertEquals(orgDtos, result.content)
        assertEquals(1, result.totalElements)
        verify { orgRepository.findAllByNameContaining(name, pageable) }
        verify { orgMapper.toDto(org) }
    }

    @Test
    fun `should handle empty name in findAll with name parameter`() {
        // Given
        val name: String? = null
        val pageable = PageRequest.of(0, 10)
        val orgs = listOf(org)
        val orgDtos = listOf(orgDto)
        val page = PageImpl(orgs, pageable, orgs.size.toLong())

        every { orgRepository.findAllByNameContaining("", pageable) } returns page
        every { orgMapper.toDto(org) } returns orgDto

        // When
        val result = orgService.findAll(name, pageable)

        // Then
        assertEquals(orgDtos, result.content)
        assertEquals(1, result.totalElements)
        verify { orgRepository.findAllByNameContaining("", pageable) }
        verify { orgMapper.toDto(org) }
    }

    @Test
    fun `should find org by id successfully`() {
        // Given
        val id = 1

        every { orgRepository.findById(id) } returns Optional.of(org)
        every { orgMapper.toDto(org) } returns orgDto

        // When
        val result = orgService.findById(id)

        // Then
        assertEquals(orgDto, result)
        verify { orgRepository.findById(id) }
        verify { orgMapper.toDto(org) }
    }

    @Test
    fun `should return null when org not found by id`() {
        // Given
        val id = 999

        every { orgRepository.findById(id) } returns Optional.empty()

        // When
        val result = orgService.findById(id)

        // Then
        assertNull(result)
        verify { orgRepository.findById(id) }
        verify(exactly = 0) { orgMapper.toDto(any()) }
    }

    @Test
    fun `should update org successfully`() {
        // Given
        val id = 1
        val updateDto = OrgDto(
            id = id,
            name = "Updated Organization",
            address = "456 Updated St",
            phone = "987-654-3210",
            city = "Updated City",
            country = "Updated Country",
            notes = "Updated Notes",
            orgTypeName = "UPDATED_TYPE"
        )
        val existingEntity = org
        val entityToUpdate = Org(
            id = id,
            name = "Updated Organization",
            address = "456 Updated St",
            phone = "987-654-3210",
            city = "Updated City",
            country = "Updated Country",
            notes = "Updated Notes",
            orgTypeName = "UPDATED_TYPE"
        )
        val updatedEntity = entityToUpdate
        val resultDto = updateDto

        every { orgRepository.findById(id) } returns Optional.of(existingEntity)
        every { orgMapper.toEntity(updateDto) } returns entityToUpdate
        every { orgRepository.save(any()) } returns updatedEntity
        every { orgMapper.toDto(updatedEntity) } returns resultDto

        // When
        val result = orgService.update(id, updateDto)

        // Then
        assertEquals(resultDto, result)
        verify { orgRepository.findById(id) }
        verify { orgMapper.toEntity(updateDto) }
        verify { orgRepository.save(any()) }
        verify { orgMapper.toDto(updatedEntity) }
    }

    @Test
    fun `should throw exception when updating non-existent org`() {
        // Given
        val id = 999
        val updateDto = OrgDto(
            id = id,
            name = "Nonexistent Organization",
            orgTypeName = "TEST_TYPE"
        )

        every { orgRepository.findById(id) } returns Optional.empty()

        // When & Then
        val exception = assertThrows<RuntimeException> {
            orgService.update(id, updateDto)
        }
        assertEquals("Org not found", exception.message)
        verify { orgRepository.findById(id) }
        verify(exactly = 0) { orgRepository.save(any()) }
    }

    @Test
    fun `should delete org successfully`() {
        // Given
        val id = 1

        every { orgRepository.deleteById(id) } just runs

        // When
        orgService.delete(id)

        // Then
        verify { orgRepository.deleteById(id) }
    }
}