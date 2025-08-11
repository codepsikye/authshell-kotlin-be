package io.cpk.be.basic.service

import io.cpk.be.basic.dto.CenterDto
import io.cpk.be.basic.entity.Center
import io.cpk.be.basic.mapper.CenterMapper
import io.cpk.be.basic.repository.AppUserRoleRepository
import io.cpk.be.basic.repository.CenterRepository
import io.mockk.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertNull

class CenterServiceTest {

    private val centerRepository = mockk<CenterRepository>()
    private val centerMapper = mockk<CenterMapper>()
    private val appUserRoleRepository = mockk<AppUserRoleRepository>()

    private lateinit var centerService: CenterService
    private lateinit var center: Center
    private lateinit var centerDto: CenterDto

    @BeforeEach
    fun setUp() {
        centerService = CenterService(centerRepository, centerMapper, appUserRoleRepository)

        center = Center(
            id = 1,
            name = "Test Center",
            address = "123 Test St",
            phone = "123-456-7890",
            org = mockk(relaxed = true) {
                every { id } returns 1
            }
        )

        centerDto = CenterDto(
            id = 1,
            name = "Test Center",
            address = "123 Test St",
            phone = "123-456-7890",
            orgId = 1
        )
    }

    @Test
    fun `should create center successfully`() {
        // Given
        val createDto = centerDto.copy(id = null)
        val savedCenter = center.copy(id = 1)

        every { centerMapper.toEntity(createDto) } returns center.copy(id = null)
        every { centerRepository.save(any()) } returns savedCenter
        every { centerMapper.toDto(savedCenter) } returns centerDto

        // When
        val result = centerService.create(createDto)

        // Then
        assertEquals(centerDto, result)
        verify { centerMapper.toEntity(createDto) }
        verify { centerRepository.save(any()) }
        verify { centerMapper.toDto(savedCenter) }
    }

    @Test
    fun `should find all centers successfully`() {
        // Given
        val centers = listOf(center)
        val centerDtos = listOf(centerDto)

        every { centerRepository.findAll() } returns centers
        every { centerMapper.toDto(center) } returns centerDto

        // When
        val result = centerService.findAll()

        // Then
        assertEquals(centerDtos, result)
        verify { centerRepository.findAll() }
        verify { centerMapper.toDto(center) }
    }

    @Test
    fun `should find center by id successfully`() {
        // Given
        every { centerRepository.findById(1) } returns Optional.of(center)
        every { centerMapper.toDto(center) } returns centerDto

        // When
        val result = centerService.findById(1)

        // Then
        assertEquals(centerDto, result)
        verify { centerRepository.findById(1) }
        verify { centerMapper.toDto(center) }
    }

    @Test
    fun `should return null when center not found by id`() {
        // Given
        every { centerRepository.findById(999) } returns Optional.empty()

        // When
        val result = centerService.findById(999)

        // Then
        assertNull(result)
        verify { centerRepository.findById(999) }
        verify { centerMapper wasNot Called }
    }

    @Test
    fun `should update center successfully`() {
        // Given
        val updateDto = centerDto.copy(name = "Updated Center")
        val existingCenter = center
        val updatedEntity = center.copy(name = "Updated Center")
        val savedCenter = updatedEntity
        val resultDto = centerDto.copy(name = "Updated Center")

        every { centerRepository.findById(1) } returns Optional.of(existingCenter)
        every { centerMapper.toEntity(updateDto) } returns updatedEntity
        every { centerRepository.save(updatedEntity.copy(id = existingCenter.id)) } returns savedCenter
        every { centerMapper.toDto(savedCenter) } returns resultDto

        // When
        val result = centerService.update(1, updateDto)

        // Then
        assertEquals(resultDto, result)
        verify { centerRepository.findById(1) }
        verify { centerMapper.toEntity(updateDto) }
        verify { centerRepository.save(any()) }
        verify { centerMapper.toDto(savedCenter) }
    }

    @Test
    fun `should throw exception when updating non-existent center`() {
        // Given
        every { centerRepository.findById(999) } returns Optional.empty()

        // When & Then
        val exception = assertThrows<RuntimeException> {
            centerService.update(999, centerDto)
        }
        assertEquals("Center not found", exception.message)
        verify { centerRepository.findById(999) }
        verify(exactly = 0) { centerRepository.save(any()) }
    }

    @Test
    fun `should delete center successfully`() {
        // Given
        every { centerRepository.deleteById(1) } just runs

        // When
        centerService.delete(1)

        // Then
        verify { centerRepository.deleteById(1) }
    }

    @Test
    fun `should find all centers with pagination`() {
        // Given
        val pageable = PageRequest.of(0, 10)
        val centers = listOf(center)
        val centerDtos = listOf(centerDto)
        val page = PageImpl(centers, pageable, centers.size.toLong())

        every { centerRepository.findAll(pageable) } returns page
        every { centerMapper.toDto(center) } returns centerDto

        // When
        val result = centerService.findAll(pageable)

        // Then
        assertEquals(centerDtos, result.content)
        assertEquals(1, result.totalElements)
        verify { centerRepository.findAll(pageable) }
        verify { centerMapper.toDto(center) }
    }

    @Test
    fun `should find all centers by orgId with pagination`() {
        // Given
        val orgId = 1
        val pageable = PageRequest.of(0, 10)
        val centers = listOf(center)
        val centerDtos = listOf(centerDto)
        val page = PageImpl(centers, pageable, centers.size.toLong())

        every { centerRepository.findAllByOrgId(orgId, pageable) } returns page
        every { centerMapper.toDto(center) } returns centerDto

        // When
        val result = centerService.findAll(orgId, pageable)

        // Then
        assertEquals(centerDtos, result.content)
        assertEquals(1, result.totalElements)
        verify { centerRepository.findAllByOrgId(orgId, pageable) }
        verify { centerMapper.toDto(center) }
    }

    @Test
    fun `should find all centers by orgId and name with pagination`() {
        // Given
        val orgId = 1
        val name = "Test"
        val pageable = PageRequest.of(0, 10)
        val centers = listOf(center)
        val centerDtos = listOf(centerDto)
        val page = PageImpl(centers, pageable, centers.size.toLong())

        every { centerRepository.findAllByOrgIdAndNameContaining(orgId, name, pageable) } returns page
        every { centerMapper.toDto(center) } returns centerDto

        // When
        val result = centerService.findAll(orgId, name, pageable)

        // Then
        assertEquals(centerDtos, result.content)
        assertEquals(1, result.totalElements)
        verify { centerRepository.findAllByOrgIdAndNameContaining(orgId, name, pageable) }
        verify { centerMapper.toDto(center) }
    }

    @Test
    fun `should handle null name in findAll with orgId and name parameters`() {
        // Given
        val orgId = 1
        val name: String? = null
        val pageable = PageRequest.of(0, 10)
        val centers = listOf(center)
        val centerDtos = listOf(centerDto)
        val page = PageImpl(centers, pageable, centers.size.toLong())

        every { centerRepository.findAllByOrgIdAndNameContaining(orgId, "", pageable) } returns page
        every { centerMapper.toDto(center) } returns centerDto

        // When
        val result = centerService.findAll(orgId, name, pageable)

        // Then
        assertEquals(centerDtos, result.content)
        assertEquals(1, result.totalElements)
        verify { centerRepository.findAllByOrgIdAndNameContaining(orgId, "", pageable) }
        verify { centerMapper.toDto(center) }
    }

    @Test
    fun `should find centers by userId`() {
        // Given
        val userId = 123
        val centerIds = listOf(1, 2, 3)
        val centers = listOf(center, center.copy(id = 2), center.copy(id = 3))
        val centerDtos = listOf(centerDto, centerDto.copy(id = 2), centerDto.copy(id = 3))

        every { appUserRoleRepository.findCenterIdsByUserId(userId) } returns centerIds
        every { centerRepository.findAllById(centerIds) } returns centers
        every { centerMapper.toDto(any()) } returns centerDto andThen centerDto.copy(id = 2) andThen centerDto.copy(id = 3)

        // When
        val result = centerService.findByUserId(userId)

        // Then
        assertEquals(centerDtos, result)
        verify { appUserRoleRepository.findCenterIdsByUserId(userId) }
        verify { centerRepository.findAllById(centerIds) }
        verify(exactly = 3) { centerMapper.toDto(any()) }
    }
}