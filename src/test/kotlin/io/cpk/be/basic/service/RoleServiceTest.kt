package io.cpk.be.basic.service

import io.cpk.be.basic.dto.RoleDto
import io.cpk.be.basic.entity.Role
import io.cpk.be.basic.mapper.RoleMapper
import io.cpk.be.basic.repository.RoleRepository
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
 * Unit tests for RoleService
 */
class RoleServiceTest {

    private val roleRepository = mockk<RoleRepository>()
    private val roleMapper = mockk<RoleMapper>()

    private lateinit var roleService: RoleService
    private lateinit var role: Role
    private lateinit var roleDto: RoleDto

    @BeforeEach
    fun setUp() {
        roleService = RoleService(roleRepository, roleMapper)

        val now = LocalDateTime.now()
        
        role = Role(
            orgId = 1,
            name = "ADMIN",
            accessRight = listOf("READ", "WRITE", "DELETE")
        )

        roleDto = RoleDto(
            orgId = 1,
            name = "ADMIN",
            accessRight = listOf("READ", "WRITE", "DELETE")
        )
    }

    @Test
    fun `should create role successfully`() {
        // Given
        val createDto = RoleDto(
            orgId = 2,
            name = "USER",
            accessRight = listOf("READ")
        )
        val entityToSave = Role(
            orgId = 2,
            name = "USER",
            accessRight = listOf("READ")
        )
        val savedEntity = entityToSave

        every { roleMapper.toEntity(createDto) } returns entityToSave
        every { roleRepository.save(entityToSave) } returns savedEntity
        every { roleMapper.toDto(savedEntity) } returns createDto

        // When
        val result = roleService.create(createDto)

        // Then
        assertEquals(createDto, result)
        verify { roleMapper.toEntity(createDto) }
        verify { roleRepository.save(entityToSave) }
        verify { roleMapper.toDto(savedEntity) }
    }

    @Test
    fun `should find all roles`() {
        // Given
        val roles = listOf(role)
        val roleDtos = listOf(roleDto)

        every { roleRepository.findAll() } returns roles
        every { roleMapper.toDto(role) } returns roleDto

        // When
        val result = roleService.findAll()

        // Then
        assertEquals(roleDtos, result)
        verify { roleRepository.findAll() }
        verify { roleMapper.toDto(role) }
    }

    @Test
    fun `should find all roles with pagination`() {
        // Given
        val pageable = PageRequest.of(0, 10)
        val roles = listOf(role)
        val roleDtos = listOf(roleDto)
        val page = PageImpl(roles, pageable, roles.size.toLong())

        every { roleRepository.findAll(pageable) } returns page
        every { roleMapper.toDto(role) } returns roleDto

        // When
        val result = roleService.findAll(pageable)

        // Then
        assertEquals(roleDtos, result.content)
        assertEquals(1, result.totalElements)
        verify { roleRepository.findAll(pageable) }
        verify { roleMapper.toDto(role) }
    }

    @Test
    fun `should find all roles by orgId with pagination`() {
        // Given
        val orgId = 1
        val pageable = PageRequest.of(0, 10)
        val roles = listOf(role)
        val roleDtos = listOf(roleDto)
        val page = PageImpl(roles, pageable, roles.size.toLong())

        every { roleRepository.findAllByOrgId(orgId, pageable) } returns page
        every { roleMapper.toDto(role) } returns roleDto

        // When
        val result = roleService.findAll(orgId, pageable)

        // Then
        assertEquals(roleDtos, result.content)
        assertEquals(1, result.totalElements)
        verify { roleRepository.findAllByOrgId(orgId, pageable) }
        verify { roleMapper.toDto(role) }
    }

    @Test
    fun `should find all roles by orgId and name with pagination`() {
        // Given
        val orgId = 1
        val name = "ADMIN"
        val pageable = PageRequest.of(0, 10)
        val roles = listOf(role)
        val roleDtos = listOf(roleDto)
        val page = PageImpl(roles, pageable, roles.size.toLong())

        every { roleRepository.findAllByOrgIdAndNameContaining(orgId, name, pageable) } returns page
        every { roleMapper.toDto(role) } returns roleDto

        // When
        val result = roleService.findAll(orgId, name, pageable)

        // Then
        assertEquals(roleDtos, result.content)
        assertEquals(1, result.totalElements)
        verify { roleRepository.findAllByOrgIdAndNameContaining(orgId, name, pageable) }
        verify { roleMapper.toDto(role) }
    }

    @Test
    fun `should handle empty name in findAll with orgId and name parameters`() {
        // Given
        val orgId = 1
        val name: String? = null
        val pageable = PageRequest.of(0, 10)
        val roles = listOf(role)
        val roleDtos = listOf(roleDto)
        val page = PageImpl(roles, pageable, roles.size.toLong())

        every { roleRepository.findAllByOrgIdAndNameContaining(orgId, "", pageable) } returns page
        every { roleMapper.toDto(role) } returns roleDto

        // When
        val result = roleService.findAll(orgId, name, pageable)

        // Then
        assertEquals(roleDtos, result.content)
        assertEquals(1, result.totalElements)
        verify { roleRepository.findAllByOrgIdAndNameContaining(orgId, "", pageable) }
        verify { roleMapper.toDto(role) }
    }

    @Test
    fun `should find role by composite id successfully`() {
        // Given
        val orgId = 1
        val name = "ADMIN"
        val compositeId = Role.RoleId(orgId, name)

        every { roleRepository.findById(compositeId) } returns Optional.of(role)
        every { roleMapper.toDto(role) } returns roleDto

        // When
        val result = roleService.findById(orgId, name)

        // Then
        assertEquals(roleDto, result)
        verify { roleRepository.findById(compositeId) }
        verify { roleMapper.toDto(role) }
    }

    @Test
    fun `should return null when role not found by composite id`() {
        // Given
        val orgId = 999
        val name = "NONEXISTENT"
        val compositeId = Role.RoleId(orgId, name)

        every { roleRepository.findById(compositeId) } returns Optional.empty()

        // When
        val result = roleService.findById(orgId, name)

        // Then
        assertNull(result)
        verify { roleRepository.findById(compositeId) }
        verify(exactly = 0) { roleMapper.toDto(any()) }
    }

    @Test
    fun `should update role successfully`() {
        // Given
        val orgId = 1
        val name = "ADMIN"
        val compositeId = Role.RoleId(orgId, name)
        
        val updateDto = RoleDto(
            orgId = 999, // This will be ignored in the update
            name = "UPDATED_ROLE", // This will be ignored in the update
            accessRight = listOf("READ", "WRITE", "DELETE", "EXECUTE")
        )
        
        val existingEntity = role
        val entityToUpdate = Role(
            orgId = 999, // This will be replaced with the existing orgId
            name = "UPDATED_ROLE", // This will be replaced with the existing name
            accessRight = listOf("READ", "WRITE", "DELETE", "EXECUTE")
        )
        
        val updatedEntity = Role(
            orgId = 1, // orgId is preserved from the existing entity
            name = "ADMIN", // name is preserved from the existing entity
            accessRight = listOf("READ", "WRITE", "DELETE", "EXECUTE")
        )
        
        val resultDto = RoleDto(
            orgId = 1,
            name = "ADMIN",
            accessRight = listOf("READ", "WRITE", "DELETE", "EXECUTE")
        )

        every { roleRepository.findById(compositeId) } returns Optional.of(existingEntity)
        every { roleMapper.toEntity(updateDto) } returns entityToUpdate
        every { roleRepository.save(any()) } returns updatedEntity
        every { roleMapper.toDto(updatedEntity) } returns resultDto

        // When
        val result = roleService.update(orgId, name, updateDto)

        // Then
        assertEquals(resultDto, result)
        verify { roleRepository.findById(compositeId) }
        verify { roleMapper.toEntity(updateDto) }
        verify { roleRepository.save(any()) }
        verify { roleMapper.toDto(updatedEntity) }
    }

    @Test
    fun `should throw exception when updating non-existent role`() {
        // Given
        val orgId = 999
        val name = "NONEXISTENT"
        val compositeId = Role.RoleId(orgId, name)
        
        val updateDto = RoleDto(
            orgId = 999,
            name = "NONEXISTENT",
            accessRight = listOf("READ", "WRITE")
        )

        every { roleRepository.findById(compositeId) } returns Optional.empty()

        // When & Then
        val exception = assertThrows<RuntimeException> {
            roleService.update(orgId, name, updateDto)
        }
        assertEquals("Role not found", exception.message)
        verify { roleRepository.findById(compositeId) }
        verify(exactly = 0) { roleRepository.save(any()) }
    }

    @Test
    fun `should delete role successfully`() {
        // Given
        val orgId = 1
        val name = "ADMIN"
        val compositeId = Role.RoleId(orgId, name)

        every { roleRepository.deleteById(compositeId) } just runs

        // When
        roleService.delete(orgId, name)

        // Then
        verify { roleRepository.deleteById(compositeId) }
    }
}