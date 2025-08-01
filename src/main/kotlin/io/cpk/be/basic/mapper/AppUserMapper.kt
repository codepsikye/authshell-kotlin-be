package io.cpk.be.basic.mapper

import io.cpk.be.basic.dto.AppUserDto
import io.cpk.be.basic.dto.UserPrefs
import io.cpk.be.basic.entity.AppUser
import org.springframework.stereotype.Component

/**
 * Mapper for converting between AppUser entity and AppUserDto
 */
@Component
class AppUserMapper {

    /**
     * Converts an AppUser entity to an AppUserDto
     */
    fun toDto(entity: AppUser): AppUserDto {
        return AppUserDto(
            id = entity.id,
            orgId = entity.orgId,
            username = entity.username,
            fullname = entity.fullname,
            title = entity.title,
            email = entity.email,
            password = null, //passwords should not be mapped from entity only to entity
            orgAdmin = entity.orgAdmin,
            userPrefs = entity.userPrefs.toMap()
        )
    }

    /**
     * Converts an AppUserDto to an AppUser entity
     */
    fun toEntity(dto: AppUserDto): AppUser {
        // Create an Org object with just the ID
        val org = if (dto.orgId > 0) io.cpk.be.basic.entity.Org(id = dto.orgId, name = "", orgTypeName = "DEFAULT", orgConfigs = emptyMap()) else null
        
        return AppUser(
            id = dto.id,
            org = org,
            username = dto.username,
            fullname = dto.fullname,
            title = dto.title,
            email = dto.email,
            password = dto.password,
            orgAdmin = dto.orgAdmin,
            userPrefs = UserPrefs.fromMap(dto.userPrefs)
        )
    }

    /**
     * Converts a list of AppUser entities to a list of AppUserDtos
     */
    fun toDtoList(entities: List<AppUser>): List<AppUserDto> {
        return entities.map { toDto(it) }
    }

    /**
     * Converts a list of AppUserDtos to a list of AppUser entities
     */
    fun toEntityList(dtos: List<AppUserDto>): List<AppUser> {
        return dtos.map { toEntity(it) }
    }
}
