package io.cpk.be.basic.mapper

import io.cpk.be.basic.dto.RoleDto
import io.cpk.be.basic.entity.Role
import org.springframework.stereotype.Component

/**
 * Mapper for converting between Role entity and RoleDto
 */
@Component
class RoleMapper {
    /**
     * Converts a Role entity to a RoleDto
     */
    fun toDto(entity: Role): RoleDto {
        return RoleDto(
            orgId = entity.orgId,
            name = entity.name,
            accessRight = entity.accessRight.toList()
        )
    }

    /**
     * Converts a RoleDto to a Role entity
     * Note: createdAt, updatedAt, and org are set to default values
     */
    fun toEntity(dto: RoleDto): Role {
        return Role(
            orgId = dto.orgId,
            name = dto.name,
            accessRight = mapAccessRights(dto.accessRight)
        )
    }

    /**
     * Converts a list of Role entities to a list of RoleDtos
     */
    fun toDtoList(entities: List<Role>): List<RoleDto> {
        return entities.map { toDto(it) }
    }

    /**
     * Converts a list of RoleDtos to a list of Role entities
     */
    fun toEntityList(dtos: List<RoleDto>): List<Role> {
        return dtos.map { toEntity(it) }
    }

    /**
     * Maps a list of access rights, handling null values
     */
    fun mapAccessRights(accessRights: List<String>?): List<String> {
        return accessRights.orEmpty()
    }
}
