package io.cpk.be.basic.mapper

import io.cpk.be.basic.dto.AppUserRoleDto
import io.cpk.be.basic.entity.AppUserRole
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

/**
 * Unit tests for AppUserRoleMapper
 */
class AppUserRoleMapperTest {

    private lateinit var appUserRoleMapper: AppUserRoleMapper
    
    @BeforeEach
    fun setUp() {
        appUserRoleMapper = AppUserRoleMapper()
    }

    @Test
    fun `should map entity to dto`() {
        // Given
        val appUserRole = AppUserRole(
            userId = "user123",
            orgId = 1,
            centerId = 2,
            roleName = "admin"
        )

        // When
        val appUserRoleDto = appUserRoleMapper.toDto(appUserRole)

        // Then
        assertEquals("user123", appUserRoleDto.userId)
        assertEquals(1, appUserRoleDto.orgId)
        assertEquals(2, appUserRoleDto.centerId)
        assertEquals("admin", appUserRoleDto.roleName)
    }

    @Test
    fun `should map dto to entity`() {
        // Given
        val appUserRoleDto = AppUserRoleDto(
            userId = "user123",
            orgId = 1,
            centerId = 2,
            roleName = "admin"
        )

        // When
        val appUserRole = appUserRoleMapper.toEntity(appUserRoleDto)

        // Then
        assertEquals("user123", appUserRole.userId)
        assertEquals(1, appUserRole.orgId)
        assertEquals(2, appUserRole.centerId)
        assertEquals("admin", appUserRole.roleName)
        assertNull(appUserRole.user)
        assertNull(appUserRole.org)
        assertNull(appUserRole.center)
        assertNull(appUserRole.role)
    }

    @Test
    fun `should map entity list to dto list`() {
        // Given
        val appUserRole1 = AppUserRole(
            userId = "user123",
            orgId = 1,
            centerId = 2,
            roleName = "admin"
        )
        val appUserRole2 = AppUserRole(
            userId = "user456",
            orgId = 1,
            centerId = 3,
            roleName = "user"
        )
        val appUserRoles = listOf(appUserRole1, appUserRole2)

        // When
        val appUserRoleDtos = appUserRoleMapper.toDtoList(appUserRoles)

        // Then
        assertEquals(2, appUserRoleDtos.size)
        assertEquals("user123", appUserRoleDtos[0].userId)
        assertEquals(1, appUserRoleDtos[0].orgId)
        assertEquals(2, appUserRoleDtos[0].centerId)
        assertEquals("admin", appUserRoleDtos[0].roleName)
        
        assertEquals("user456", appUserRoleDtos[1].userId)
        assertEquals(1, appUserRoleDtos[1].orgId)
        assertEquals(3, appUserRoleDtos[1].centerId)
        assertEquals("user", appUserRoleDtos[1].roleName)
    }

    @Test
    fun `should map dto list to entity list`() {
        // Given
        val appUserRoleDto1 = AppUserRoleDto(
            userId = "user123",
            orgId = 1,
            centerId = 2,
            roleName = "admin"
        )
        val appUserRoleDto2 = AppUserRoleDto(
            userId = "user456",
            orgId = 1,
            centerId = 3,
            roleName = "user"
        )
        val appUserRoleDtos = listOf(appUserRoleDto1, appUserRoleDto2)

        // When
        val appUserRoles = appUserRoleMapper.toEntityList(appUserRoleDtos)

        // Then
        assertEquals(2, appUserRoles.size)
        assertEquals("user123", appUserRoles[0].userId)
        assertEquals(1, appUserRoles[0].orgId)
        assertEquals(2, appUserRoles[0].centerId)
        assertEquals("admin", appUserRoles[0].roleName)
        assertNull(appUserRoles[0].user)
        assertNull(appUserRoles[0].org)
        assertNull(appUserRoles[0].center)
        assertNull(appUserRoles[0].role)
        
        assertEquals("user456", appUserRoles[1].userId)
        assertEquals(1, appUserRoles[1].orgId)
        assertEquals(3, appUserRoles[1].centerId)
        assertEquals("user", appUserRoles[1].roleName)
        assertNull(appUserRoles[1].user)
        assertNull(appUserRoles[1].org)
        assertNull(appUserRoles[1].center)
        assertNull(appUserRoles[1].role)
    }
}