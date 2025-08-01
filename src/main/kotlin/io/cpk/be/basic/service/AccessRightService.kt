package io.cpk.be.basic.service

import io.cpk.be.basic.dto.AccessRightDto
import io.cpk.be.basic.entity.AccessRight
import io.cpk.be.basic.mapper.AccessRightMapper
import io.cpk.be.basic.repository.AccessRightRepository
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
@Transactional
class AccessRightService(
    private val accessRightRepository: AccessRightRepository,
    private val accessRightMapper: AccessRightMapper
) {
    fun create(accessRightDto: AccessRightDto): AccessRightDto {
        val accessRight = accessRightMapper.toEntity(accessRightDto)
        val savedAccessRight = accessRightRepository.save(accessRight)
        return accessRightMapper.toDto(savedAccessRight)
    }

    @Transactional(readOnly = true)
    fun findAll(): List<AccessRightDto> {
        return accessRightRepository.findAll().map(accessRightMapper::toDto)
    }

    @Transactional(readOnly = true)
    fun findAll(pageable: Pageable): Page<AccessRightDto> {
        val accessRightPage = accessRightRepository.findAll(pageable)
        val accessRightDtos = accessRightPage.content.map(accessRightMapper::toDto)
        return PageImpl(accessRightDtos, pageable, accessRightPage.totalElements)
    }

    @Transactional(readOnly = true)
    fun findAll(name: String?, pageable: Pageable): Page<AccessRightDto> {
        val accessRightPage = accessRightRepository.findAllByNameContaining(name.orEmpty(), pageable)
        val accessRightDtos = accessRightPage.content.map(accessRightMapper::toDto)
        return PageImpl(accessRightDtos, pageable, accessRightPage.totalElements)
    }

    @Transactional(readOnly = true)
    fun findById(id: String): AccessRightDto? {
        return accessRightRepository.findById(id).map(accessRightMapper::toDto).orElse(null)
    }

    fun update(id: String, accessRightDto: AccessRightDto): AccessRightDto {
        val existingAccessRight =
            accessRightRepository.findById(id).orElseThrow {
                RuntimeException("AccessRight not found")
            }
        val updatedAccessRight = existingAccessRight.copy(name = accessRightDto.name!!)
        return accessRightMapper.toDto(accessRightRepository.save(updatedAccessRight))
    }

    fun delete(id: String) {
        accessRightRepository.deleteById(id)
    }
}