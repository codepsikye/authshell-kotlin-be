package io.cpk.be.basic.service

import io.cpk.be.basic.dto.CenterDto
import io.cpk.be.basic.mapper.CenterMapper
import io.cpk.be.basic.repository.AppUserRoleRepository
import io.cpk.be.basic.repository.CenterRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class CenterService(
    private val centerRepository: CenterRepository,
    private val centerMapper: CenterMapper,
    private val appUserRoleRepository: AppUserRoleRepository
) {
    fun create(centerDto: CenterDto): CenterDto {
        val center = centerMapper.toEntity(centerDto)
        val savedCenter = centerRepository.save(center)
        return centerMapper.toDto(savedCenter)
    }

    @Transactional(readOnly = true)
    fun findAll(): List<CenterDto> {
        return centerRepository.findAll().map(centerMapper::toDto)
    }

    @Transactional(readOnly = true)
    fun findAll(pageable: Pageable): Page<CenterDto> {
        val centerPage = centerRepository.findAll(pageable)
        val centerDtos = centerPage.content.map(centerMapper::toDto)
        return PageImpl(centerDtos, pageable, centerPage.totalElements)
    }

    @Transactional(readOnly = true)
    fun findAll(orgId: Int, pageable: Pageable): Page<CenterDto> {
        val centerPage = centerRepository.findAllByOrgId(orgId, pageable)
        val centerDtos = centerPage.content.map(centerMapper::toDto)
        return PageImpl(centerDtos, pageable, centerPage.totalElements)
    }

    @Transactional(readOnly = true)
    fun findAll(orgId: Int, name: String?, pageable: Pageable): Page<CenterDto> {
        val centerPage = centerRepository.findAllByOrgIdAndNameContaining(orgId, name.orEmpty(), pageable)
        val centerDtos = centerPage.content.map(centerMapper::toDto)
        return PageImpl(centerDtos, pageable, centerPage.totalElements)
    }

    @Transactional(readOnly = true)
    fun findById(id: Int): CenterDto? {
        return centerRepository.findById(id).map(centerMapper::toDto).orElse(null)
    }

    fun update(id: Int, centerDto: CenterDto): CenterDto {
        val existingCenter = centerRepository.findById(id).orElseThrow { RuntimeException("Center not found") }
        val updatedCenter = centerMapper.toEntity(centerDto).copy(id = existingCenter.id)
        return centerMapper.toDto(centerRepository.save(updatedCenter))
    }

    fun delete(id: Int) {
        centerRepository.deleteById(id)
    }

    @Transactional(readOnly = true)
    fun findByUserId(userId: String): List<CenterDto> {
        val centerIds = appUserRoleRepository.findCenterIdsByUserId(userId)
        return centerRepository.findAllById(centerIds).map(centerMapper::toDto)
    }
}