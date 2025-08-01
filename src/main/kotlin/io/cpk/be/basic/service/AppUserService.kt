package io.cpk.be.basic.service

import io.cpk.be.basic.dto.AppUserDto
import io.cpk.be.basic.mapper.AppUserMapper
import io.cpk.be.basic.repository.AppUserRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class AppUserService(
    private val appUserRepository: AppUserRepository,
    private val appUserMapper: AppUserMapper,
    private val passwordEncoder: PasswordEncoder
) {
    fun create(appUserDto: AppUserDto): AppUserDto {
        val appUser = appUserMapper.toEntity(appUserDto).copy(
            password = appUserDto.password?.let { passwordEncoder.encode(it) }
        )
        val savedAppUser = appUserRepository.save(appUser)
        return appUserMapper.toDto(savedAppUser)
    }

    @Transactional(readOnly = true)
    fun findAll(): List<AppUserDto> {
        return appUserRepository.findAll().map(appUserMapper::toDto)
    }

    @Transactional(readOnly = true)
    fun findAll(orgId: Int, pageable: Pageable): Page<AppUserDto> {
        val userPage = appUserRepository.findAllByOrgId(orgId, pageable)
        val userDtos = userPage.content.map(appUserMapper::toDto)
        return PageImpl(userDtos, pageable, userPage.totalElements)
    }

    @Transactional(readOnly = true)
    fun findAll(orgId: Int, fullname: String?, pageable: Pageable): Page<AppUserDto> {
        val userPage = appUserRepository.findAllByOrgIdAndFullnameContaining(orgId, fullname.orEmpty(), pageable)
        val userDtos = userPage.content.map(appUserMapper::toDto)
        return PageImpl(userDtos, pageable, userPage.totalElements)
    }

    @Transactional(readOnly = true)
    fun findById(id: String): AppUserDto? {
        return appUserRepository.findById(id).map(appUserMapper::toDto).orElse(null)
    }

    fun update(id: String, appUserDto: AppUserDto): AppUserDto {
        val existingAppUser = appUserRepository.findById(id).orElseThrow { RuntimeException("AppUser not found") }
        val updatedAppUser = appUserMapper.toEntity(appUserDto).copy(
            id = existingAppUser.id,
            password = appUserDto.password?.let { passwordEncoder.encode(it) } ?: existingAppUser.password
        )
        return appUserMapper.toDto(appUserRepository.save(updatedAppUser))
    }

    fun delete(id: String) {
        appUserRepository.deleteById(id)
    }
}