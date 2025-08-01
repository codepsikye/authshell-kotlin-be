package io.cpk.be.basic.mapper

import io.cpk.be.basic.dto.AppUserDto
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
            orgAdmin = entity.orgAdmin
        )
    }

    /**
     * Converts an AppUserDto to an AppUser entity
     */
    fun toEntity(dto: AppUserDto): AppUser {
        return AppUser(
            id = dto.id,
            orgId = dto.orgId,
            username = dto.username,
            fullname = dto.fullname,
            title = dto.title,
            email = dto.email,
            password = dto.password,
            orgAdmin = dto.orgAdmin
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
