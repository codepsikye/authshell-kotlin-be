package io.cpk.be.basic.mapper

import io.cpk.be.basic.dto.OrgTypeDto
import io.cpk.be.basic.entity.OrgType
import org.springframework.stereotype.Component

/**
 * Mapper for converting between OrgType entity and OrgTypeDto
 */
@Component
class OrgTypeMapper {
    /**
     * Converts an OrgType entity to an OrgTypeDto
     */
    fun toDto(entity: OrgType): OrgTypeDto {
        return OrgTypeDto(
            name = entity.name,
            accessRight = entity.accessRight,
            orgConfigs = entity.orgConfigs
        )
    }

    /**
     * Converts an OrgTypeDto to an OrgType entity
     * Note: createdAt and updatedAt are set to default values
     */
    fun toEntity(dto: OrgTypeDto): OrgType {
        return OrgType(
            name = dto.name,
            accessRight = dto.accessRight,
            orgConfigs = dto.orgConfigs
        )
    }

    /**
     * Converts a list of OrgType entities to a list of OrgTypeDtos
     */
    fun toDtoList(entities: List<OrgType>): List<OrgTypeDto> {
        return entities.map { toDto(it) }
    }

    /**
     * Converts a list of OrgTypeDtos to a list of OrgType entities
     */
    fun toEntityList(dtos: List<OrgTypeDto>): List<OrgType> {
        return dtos.map { toEntity(it) }
    }
}
