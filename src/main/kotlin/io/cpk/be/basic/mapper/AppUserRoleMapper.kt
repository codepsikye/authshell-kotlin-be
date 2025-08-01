package io.cpk.be.basic.mapper

import io.cpk.be.basic.dto.AppUserRoleDto
import io.cpk.be.basic.entity.AppUserRole
import org.springframework.stereotype.Component

/**
 * Mapper for converting between AppUserRole entity and AppUserRoleDto
 */
@Component
class AppUserRoleMapper {
    /**
     * Converts an AppUserRole entity to an AppUserRoleDto
     */
    fun toDto(entity: AppUserRole): AppUserRoleDto {
        return AppUserRoleDto(
            userId = entity.userId,
            orgId = entity.orgId,
            centerId = entity.centerId,
            roleName = entity.roleName
        )
    }

    /**
     * Converts an AppUserRoleDto to an AppUserRole entity
     * Note: relationship fields (user, org, center, role) are set to null
     * and createdAt/updatedAt are set to default values
     */
    fun toEntity(dto: AppUserRoleDto): AppUserRole {
        return AppUserRole(
            userId = dto.userId,
            orgId = dto.orgId,
            centerId = dto.centerId,
            roleName = dto.roleName
        )
    }

    /**
     * Converts a list of AppUserRole entities to a list of AppUserRoleDtos
     */
    fun toDtoList(entities: List<AppUserRole>): List<AppUserRoleDto> {
        return entities.map { toDto(it) }
    }

    /**
     * Converts a list of AppUserRoleDtos to a list of AppUserRole entities
     */
    fun toEntityList(dtos: List<AppUserRoleDto>): List<AppUserRole> {
        return dtos.map { toEntity(it) }
    }
}