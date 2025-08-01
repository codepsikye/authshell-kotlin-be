package io.cpk.be.basic.mapper

import io.cpk.be.basic.dto.AccessRightDto
import io.cpk.be.basic.entity.AccessRight
import org.springframework.stereotype.Component

/**
 * Mapper for converting between AccessRight entity and AccessRightDto
 */
@Component
class AccessRightMapper {

    /**
     * Converts an AccessRight entity to an AccessRightDto
     */
    fun toDto(entity: AccessRight): AccessRightDto {
        return AccessRightDto(
            name = entity.name
        )
    }

    /**
     * Converts an AccessRightDto to an AccessRight entity
     * Note: createdAt and updatedAt are set to default values
     */
    fun toEntity(dto: AccessRightDto): AccessRight {
        return AccessRight(
            name = dto.name
        )
    }

    /**
     * Converts a list of AccessRight entities to a list of AccessRightDtos
     */
    fun toDtoList(entities: List<AccessRight>): List<AccessRightDto> {
        return entities.map { toDto(it) }
    }

    /**
     * Converts a list of AccessRightDtos to a list of AccessRight entities
     */
    fun toEntityList(dtos: List<AccessRightDto>): List<AccessRight> {
        return dtos.map { toEntity(it) }
    }
}
