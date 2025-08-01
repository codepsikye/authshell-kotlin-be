package io.cpk.be.basic.mapper

import io.cpk.be.basic.dto.RoleDto
import io.cpk.be.basic.entity.Role
import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Unit tests for RoleMapper
 */
class RoleMapperTest {

    private val roleMapper = RoleMapper()

    @Test
    fun `should map entity to dto`() {
        // Given
        val accessRights = listOf("user_read", "user_create", "user_edit")
        
        val role = Role(
            orgId = 1,
            name = "admin",
            accessRight = accessRights
        )

        // When
        val roleDto = roleMapper.toDto(role)

        // Then
        assertEquals(1, roleDto.orgId)
        assertEquals("admin", roleDto.name)
        assertEquals(accessRights, roleDto.accessRight)
    }

    @Test
    fun `should map dto to entity`() {
        // Given
        val accessRights = listOf("user_read", "user_create", "user_edit")
        val now = LocalDateTime.now()

        val roleDto = RoleDto(
            orgId = 1,
            name = "admin",
            accessRight = accessRights
        )
        val roleDto2 = RoleDto(
            orgId = 1,
            name = "admin",
            accessRight = emptyList()
        )

        // When
        val role = roleMapper.toEntity(roleDto)
        val role2 = roleMapper.toEntity(roleDto2)

        // Then
        assertEquals(1, role.orgId)
        assertEquals("admin", role.name)
        assertEquals(accessRights, role.accessRight)
        // org should be null as it's ignored in mapping
        assertEquals(null, role.org)
        assertTrue(emptyList<String>() == role2.accessRight)
    }

    @Test
    fun `should map entity list to dto list`() {
        // Given
        val role1 = Role(
            orgId = 1,
            name = "admin",
            accessRight = listOf("user_read", "user_create", "user_edit")
        )
        val role2 = Role(
            orgId = 1,
            name = "user",
            accessRight = listOf("user_read")
        )
        val roles = listOf(role1, role2)

        // When
        val roleDtos = roleMapper.toDtoList(roles)

        // Then
        assertEquals(2, roleDtos.size)
        assertEquals(1, roleDtos[0].orgId)
        assertEquals("admin", roleDtos[0].name)
        assertEquals(listOf("user_read", "user_create", "user_edit"), roleDtos[0].accessRight)
        
        assertEquals(1, roleDtos[1].orgId)
        assertEquals("user", roleDtos[1].name)
        assertEquals(listOf("user_read"), roleDtos[1].accessRight)
    }

    @Test
    fun `should map dto list to entity list`() {
        // Given
        val roleDto1 = RoleDto(
            orgId = 1,
            name = "admin",
            accessRight = listOf("user_read", "user_create", "user_edit")
        )
        val roleDto2 = RoleDto(
            orgId = 1,
            name = "user",
            accessRight = emptyList()
        )
        val roleDtos = listOf(roleDto1, roleDto2)

        // When
        val roles = roleMapper.toEntityList(roleDtos)

        // Then
        assertEquals(2, roles.size)
        assertEquals(1, roles[0].orgId)
        assertEquals("admin", roles[0].name)
        assertEquals(listOf("user_read", "user_create", "user_edit"), roles[0].accessRight)
        // org should be null as it's ignored in mapping
        assertEquals(null, roles[0].org)
        
        assertEquals(1, roles[1].orgId)
        assertEquals("user", roles[1].name)
        assertEquals(emptyList(), roles[1].accessRight)
        // org should be null as it's ignored in mapping
        assertEquals(null, roles[1].org)
    }

    @Test
    fun `should handle empty access rights correctly`() {
        // Given
        val role = Role(
            orgId = 1,
            name = "guest",
            accessRight = emptyList()
        )

        // When
        val roleDto = roleMapper.toDto(role)

        // Then
        assertEquals(1, roleDto.orgId)
        assertEquals("guest", roleDto.name)
        assertEquals(emptyList(), roleDto.accessRight)
    }

    @Test
    fun `should handle empty access rights in DTO`() {
        // Given
        val roleDto = RoleDto(
            orgId = 1,
            name = "guest",
            accessRight = emptyList()
        )

        // When
        val role = roleMapper.toEntity(roleDto)

        // Then
        assertEquals(1, role.orgId)
        assertEquals("guest", role.name)
        assertEquals(emptyList(), role.accessRight)
    }
    
    @Test
    fun `should map access rights correctly`() {
        // Given
        val nonNullList = listOf("user_read", "user_create", "user_edit")
        val emptyList = emptyList<String>()
        val nullList: List<String>? = null
        
        // When
        val nonNullResult = roleMapper.mapAccessRights(nonNullList)
        val emptyResult = roleMapper.mapAccessRights(emptyList)
        val nullResult = roleMapper.mapAccessRights(nullList)
        
        // Then
        assertEquals(nonNullList, nonNullResult, "Non-null list should be returned as is")
        assertEquals(emptyList, emptyResult, "Empty list should be returned as is")
        assertEquals(emptyList(), nullResult, "Null list should be converted to empty list")
    }
}