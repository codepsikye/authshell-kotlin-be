package io.cpk.be.basic.mapper

import io.cpk.be.basic.dto.OrgDto
import io.cpk.be.basic.entity.Org
import org.springframework.stereotype.Component

/**
 * Mapper for converting between Org entity and OrgDto
 */
@Component
class OrgMapper {

    /**
     * Converts an Org entity to an OrgDto
     */
    fun toDto(entity: Org): OrgDto {
        return OrgDto(
            id = entity.id,
            name = entity.name,
            address = entity.address,
            phone = entity.phone,
            city = entity.city,
            country = entity.country,
            notes = entity.notes,
            orgTypeName = entity.orgTypeName,
            orgConfigs = entity.orgConfigs.toMap()
        )
    }

    /**
     * Converts an OrgDto to an Org entity
     * Note: orgType relationship is set to null and createdAt/updatedAt are set to default values
     */
    fun toEntity(dto: OrgDto): Org {
        return Org(
            id = dto.id,
            name = dto.name,
            address = dto.address,
            phone = dto.phone,
            city = dto.city,
            country = dto.country,
            notes = dto.notes,
            orgTypeName = dto.orgTypeName,
            orgConfigs = dto.orgConfigs
        )
    }

    /**
     * Converts a list of Org entities to a list of OrgDtos
     */
    fun toDtoList(entities: List<Org>): List<OrgDto> {
        return entities.map { toDto(it) }
    }

    /**
     * Converts a list of OrgDtos to a list of Org entities
     */
    fun toEntityList(dtos: List<OrgDto>): List<Org> {
        return dtos.map { toEntity(it) }
    }
}