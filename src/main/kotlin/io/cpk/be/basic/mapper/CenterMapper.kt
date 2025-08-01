package io.cpk.be.basic.mapper

import io.cpk.be.basic.dto.CenterDto
import io.cpk.be.basic.entity.Center
import io.cpk.be.basic.entity.Org
import io.cpk.be.basic.repository.OrgRepository
import org.springframework.stereotype.Component

/**
 * Mapper for converting between Center entity and CenterDto
 */
@Component
class CenterMapper(private val orgRepository: OrgRepository) {

    /**
     * Converts a Center entity to a CenterDto
     */
    fun toDto(entity: Center): CenterDto {
        return CenterDto(
            id = entity.id,
            name = entity.name,
            address = entity.address,
            phone = entity.phone,
            orgId = entity.org?.id ?: 0
        )
    }

    /**
     * Converts a CenterDto to a Center entity
     * Note: org relationship is set to null and createdAt/updatedAt are set to default values
     */
    fun toEntity(dto: CenterDto): Center {
        val org = if (dto.orgId > 0) orgRepository.findById(dto.orgId).orElse(null) else null
        return Center(
            id = dto.id,
            name = dto.name,
            address = dto.address,
            phone = dto.phone,
            org = org
        )
    }

    /**
     * Converts a list of Center entities to a list of CenterDtos
     */
    fun toDtoList(entities: List<Center>): List<CenterDto> {
        return entities.map { toDto(it) }
    }

    /**
     * Converts a list of CenterDtos to a list of Center entities
     */
    fun toEntityList(dtos: List<CenterDto>): List<Center> {
        return dtos.map { toEntity(it) }
    }
}
