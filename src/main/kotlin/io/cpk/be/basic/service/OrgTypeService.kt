package io.cpk.be.basic.service

import io.cpk.be.basic.dto.OrgTypeDto
import io.cpk.be.basic.mapper.OrgTypeMapper
import io.cpk.be.basic.repository.OrgTypeRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class OrgTypeService(
    private val orgTypeRepository: OrgTypeRepository,
    private val orgTypeMapper: OrgTypeMapper
) {
    fun create(orgTypeDto: OrgTypeDto): OrgTypeDto {
        val orgType = orgTypeMapper.toEntity(orgTypeDto)
        val savedOrgType = orgTypeRepository.save(orgType)
        return orgTypeMapper.toDto(savedOrgType)
    }

    @Transactional(readOnly = true)
    fun findAll(): List<OrgTypeDto> {
        return orgTypeRepository.findAll().map(orgTypeMapper::toDto)
    }

    @Transactional(readOnly = true)
    fun findAll(pageable: Pageable): Page<OrgTypeDto> {
        val orgTypePage = orgTypeRepository.findAll(pageable)
        val orgTypeDtos = orgTypePage.content.map(orgTypeMapper::toDto)
        return PageImpl(orgTypeDtos, pageable, orgTypePage.totalElements)
    }

    @Transactional(readOnly = true)
    fun findAll(name: String?, pageable: Pageable): Page<OrgTypeDto> {
        val orgTypePage = orgTypeRepository.findAllByNameContaining(name.orEmpty(), pageable)
        val orgTypeDtos = orgTypePage.content.map(orgTypeMapper::toDto)
        return PageImpl(orgTypeDtos, pageable, orgTypePage.totalElements)
    }

    @Transactional(readOnly = true)
    fun findById(id: String): OrgTypeDto? {
        return orgTypeRepository.findById(id).map(orgTypeMapper::toDto).orElse(null)
    }

    fun update(id: String, orgTypeDto: OrgTypeDto): OrgTypeDto {
        val existingOrgType = orgTypeRepository.findById(id).orElseThrow { NoSuchElementException("OrgType not found") }
        val updatedOrgType = orgTypeMapper.toEntity(orgTypeDto).copy(name = existingOrgType.name)
        return orgTypeMapper.toDto(orgTypeRepository.save(updatedOrgType))
    }

    fun delete(id: String) {
        if (!orgTypeRepository.existsById(id)) {
            throw NoSuchElementException("OrgType not found")
        }
        orgTypeRepository.deleteById(id)
    }
}