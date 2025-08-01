package io.cpk.be.basic.service

import io.cpk.be.basic.dto.OrgDto
import io.cpk.be.basic.mapper.OrgMapper
import io.cpk.be.basic.repository.OrgRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class OrgService(
    private val orgRepository: OrgRepository,
    private val orgMapper: OrgMapper
) {
    fun create(orgDto: OrgDto): OrgDto {
        val org = orgMapper.toEntity(orgDto)
        val savedOrg = orgRepository.save(org)
        return orgMapper.toDto(savedOrg)
    }

    @Transactional(readOnly = true)
    fun findAll(): List<OrgDto> {
        return orgRepository.findAll().map(orgMapper::toDto)
    }

    @Transactional(readOnly = true)
    fun findAll(pageable: Pageable): Page<OrgDto> {
        val orgPage = orgRepository.findAll(pageable)
        val orgDtos = orgPage.content.map(orgMapper::toDto)
        return PageImpl(orgDtos, pageable, orgPage.totalElements)
    }

    @Transactional(readOnly = true)
    fun findAll(name: String?, pageable: Pageable): Page<OrgDto> {
        val orgPage = orgRepository.findAllByNameContaining(name.orEmpty(), pageable)
        val orgDtos = orgPage.content.map(orgMapper::toDto)
        return PageImpl(orgDtos, pageable, orgPage.totalElements)
    }

    @Transactional(readOnly = true)
    fun findById(id: Int): OrgDto? {
        return orgRepository.findById(id).map(orgMapper::toDto).orElse(null)
    }

    fun update(id: Int, orgDto: OrgDto): OrgDto {
        val existingOrg = orgRepository.findById(id).orElseThrow { RuntimeException("Org not found") }
        val updatedOrg = orgMapper.toEntity(orgDto).copy(id = existingOrg.id)
        return orgMapper.toDto(orgRepository.save(updatedOrg))
    }

    fun delete(id: Int) {
        orgRepository.deleteById(id)
    }
}