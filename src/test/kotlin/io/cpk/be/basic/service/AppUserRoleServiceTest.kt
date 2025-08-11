package io.cpk.be.basic.service

import io.cpk.be.basic.dto.AppUserRoleDto
import io.cpk.be.basic.entity.AppUserRole
import io.cpk.be.basic.mapper.AppUserRoleMapper
import io.cpk.be.basic.repository.AppUserRoleRepository
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
 * Unit tests for AppUserRoleService
 */
class AppUserRoleServiceTest {

    private val appUserRoleRepository = mockk<AppUserRoleRepository>()
    private val appUserRoleMapper = mockk<AppUserRoleMapper>()

    private lateinit var appUserRoleService: AppUserRoleService
    private lateinit var appUserRole: AppUserRole
    private lateinit var appUserRoleDto: AppUserRoleDto

    @BeforeEach
    fun setUp() {
        appUserRoleService = AppUserRoleService(appUserRoleRepository, appUserRoleMapper)

        appUserRole = AppUserRole(
            userId = 123,
            orgId = 1,
            centerId = 2,
            roleName = "ADMIN"
        )

        appUserRoleDto = AppUserRoleDto(
            userId = 123,
            orgId = 1,
            centerId = 2,
            roleName = "ADMIN"
        )
    }

    @Test
    fun `should create app user role successfully`() {
        // Given
        val createDto = AppUserRoleDto(
            userId = 456,
            orgId = 3,
            centerId = 4,
            roleName = "USER"
        )
        val entityToSave = AppUserRole(
            userId = 456,
            orgId = 3,
            centerId = 4,
            roleName = "USER"
        )
        val savedEntity = entityToSave

        every { appUserRoleMapper.toEntity(createDto) } returns entityToSave
        every { appUserRoleRepository.save(entityToSave) } returns savedEntity
        every { appUserRoleMapper.toDto(savedEntity) } returns createDto

        // When
        val result = appUserRoleService.create(createDto)

        // Then
        assertEquals(createDto, result)
        verify { appUserRoleMapper.toEntity(createDto) }
        verify { appUserRoleRepository.save(entityToSave) }
        verify { appUserRoleMapper.toDto(savedEntity) }
    }

    @Test
    fun `should find all app user roles`() {
        // Given
        val appUserRoles = listOf(appUserRole)
        val appUserRoleDtos = listOf(appUserRoleDto)

        every { appUserRoleRepository.findAll() } returns appUserRoles
        every { appUserRoleMapper.toDto(appUserRole) } returns appUserRoleDto

        // When
        val result = appUserRoleService.findAll()

        // Then
        assertEquals(appUserRoleDtos, result)
        verify { appUserRoleRepository.findAll() }
        verify { appUserRoleMapper.toDto(appUserRole) }
    }

    @Test
    fun `should find all app user roles with pagination`() {
        // Given
        val pageable = PageRequest.of(0, 10)
        val appUserRoles = listOf(appUserRole)
        val appUserRoleDtos = listOf(appUserRoleDto)
        val page = PageImpl(appUserRoles, pageable, appUserRoles.size.toLong())

        every { appUserRoleRepository.findAll(pageable) } returns page
        every { appUserRoleMapper.toDto(appUserRole) } returns appUserRoleDto

        // When
        val result = appUserRoleService.findAll(pageable)

        // Then
        assertEquals(appUserRoleDtos, result.content)
        assertEquals(1, result.totalElements)
        verify { appUserRoleRepository.findAll(pageable) }
        verify { appUserRoleMapper.toDto(appUserRole) }
    }

    @Test
    fun `should find app user role by composite id successfully`() {
        // Given
        val userId = 123
        val orgId = 1
        val centerId = 2
        val roleName = "ADMIN"
        val compositeId = AppUserRole.AppUserRoleId(userId, orgId, centerId, roleName)

        every { appUserRoleRepository.findById(compositeId) } returns Optional.of(appUserRole)
        every { appUserRoleMapper.toDto(appUserRole) } returns appUserRoleDto

        // When
        val result = appUserRoleService.findById(userId, orgId, centerId, roleName)

        // Then
        assertEquals(appUserRoleDto, result)
        verify { appUserRoleRepository.findById(compositeId) }
        verify { appUserRoleMapper.toDto(appUserRole) }
    }

    @Test
    fun `should return null when app user role not found by composite id`() {
        // Given
        val userId = 999
        val orgId = 99
        val centerId = 99
        val roleName = "NONEXISTENT"
        val compositeId = AppUserRole.AppUserRoleId(userId, orgId, centerId, roleName)

        every { appUserRoleRepository.findById(compositeId) } returns Optional.empty()

        // When
        val result = appUserRoleService.findById(userId, orgId, centerId, roleName)

        // Then
        assertNull(result)
        verify { appUserRoleRepository.findById(compositeId) }
        verify(exactly = 0) { appUserRoleMapper.toDto(any()) }
    }

    @Test
    fun `should update app user role successfully`() {
        // Given
        val userId = 123
        val orgId = 1
        val centerId = 2
        val roleName = "ADMIN"
        val compositeId = AppUserRole.AppUserRoleId(userId, orgId, centerId, roleName)
        
        // Updated DTO with same IDs but potentially different relationships
        val updateDto = AppUserRoleDto(
            userId = userId,
            orgId = orgId,
            centerId = centerId,
            roleName = roleName
        )
        
        val existingEntity = appUserRole
        val entityToUpdate = AppUserRole(
            userId = userId,
            orgId = orgId,
            centerId = centerId,
            roleName = roleName
        )
        val updatedEntity = entityToUpdate
        val resultDto = updateDto

        every { appUserRoleRepository.findById(compositeId) } returns Optional.of(existingEntity)
        every { appUserRoleMapper.toEntity(updateDto) } returns entityToUpdate
        every { appUserRoleRepository.save(any()) } returns updatedEntity
        every { appUserRoleMapper.toDto(updatedEntity) } returns resultDto

        // When
        val result = appUserRoleService.update(userId, orgId, centerId, roleName, updateDto)

        // Then
        assertEquals(resultDto, result)
        verify { appUserRoleRepository.findById(compositeId) }
        verify { appUserRoleMapper.toEntity(updateDto) }
        verify { appUserRoleRepository.save(any()) }
        verify { appUserRoleMapper.toDto(updatedEntity) }
    }

    @Test
    fun `should throw exception when updating non-existent app user role`() {
        // Given
        val userId = 999
        val orgId = 99
        val centerId = 99
        val roleName = "NONEXISTENT"
        val compositeId = AppUserRole.AppUserRoleId(userId, orgId, centerId, roleName)
        
        val updateDto = AppUserRoleDto(
            userId = userId,
            orgId = orgId,
            centerId = centerId,
            roleName = roleName
        )

        every { appUserRoleRepository.findById(compositeId) } returns Optional.empty()

        // When & Then
        val exception = assertThrows<RuntimeException> {
            appUserRoleService.update(userId, orgId, centerId, roleName, updateDto)
        }
        assertEquals("AppUserRole not found", exception.message)
        verify { appUserRoleRepository.findById(compositeId) }
        verify(exactly = 0) { appUserRoleRepository.save(any()) }
    }

    @Test
    fun `should delete app user role successfully`() {
        // Given
        val userId = 123
        val orgId = 1
        val centerId = 2
        val roleName = "ADMIN"
        val compositeId = AppUserRole.AppUserRoleId(userId, orgId, centerId, roleName)

        every { appUserRoleRepository.deleteById(compositeId) } just runs

        // When
        appUserRoleService.delete(userId, orgId, centerId, roleName)

        // Then
        verify { appUserRoleRepository.deleteById(compositeId) }
    }
}