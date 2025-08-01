package io.cpk.be.basic.service

import io.cpk.be.basic.dto.AppUserRoleDto
import io.cpk.be.basic.entity.AppUserRole
import io.cpk.be.basic.mapper.AppUserRoleMapper
import io.cpk.be.basic.repository.AppUserRoleRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class AppUserRoleService(
    private val appUserRoleRepository: AppUserRoleRepository,
    private val appUserRoleMapper: AppUserRoleMapper
) {
    fun create(appUserRoleDto: AppUserRoleDto): AppUserRoleDto {
        val appUserRole = appUserRoleMapper.toEntity(appUserRoleDto)
        val savedAppUserRole = appUserRoleRepository.save(appUserRole)
        return appUserRoleMapper.toDto(savedAppUserRole)
    }

    @Transactional(readOnly = true)
    fun findAll(): List<AppUserRoleDto> {
        return appUserRoleRepository.findAll().map(appUserRoleMapper::toDto)
    }

    @Transactional(readOnly = true)
    fun findAll(pageable: Pageable): Page<AppUserRoleDto> {
        val appUserRolePage = appUserRoleRepository.findAll(pageable)
        val appUserRoleDtos = appUserRolePage.content.map(appUserRoleMapper::toDto)
        return PageImpl(appUserRoleDtos, pageable, appUserRolePage.totalElements)
    }

    @Transactional(readOnly = true)
    fun findById(userId: String, orgId: Int, centerId: Int, roleName: String): AppUserRoleDto? {
        return appUserRoleRepository.findById(AppUserRole.AppUserRoleId(userId, orgId, centerId, roleName))
            .map(appUserRoleMapper::toDto).orElse(null)
    }

    fun update(
        userId: String,
        orgId: Int,
        centerId: Int,
        roleName: String,
        appUserRoleDto: AppUserRoleDto
    ): AppUserRoleDto {
        val existingAppUserRole =
            appUserRoleRepository.findById(AppUserRole.AppUserRoleId(userId, orgId, centerId, roleName))
                .orElseThrow { RuntimeException("AppUserRole not found") }
        val updatedAppUserRole = appUserRoleMapper.toEntity(appUserRoleDto).copy(
            userId = existingAppUserRole.userId,
            orgId = existingAppUserRole.orgId,
            centerId = existingAppUserRole.centerId,
            roleName = existingAppUserRole.roleName
        )
        return appUserRoleMapper.toDto(appUserRoleRepository.save(updatedAppUserRole))
    }

    fun delete(userId: String, orgId: Int, centerId: Int, roleName: String) {
        appUserRoleRepository.deleteById(AppUserRole.AppUserRoleId(userId, orgId, centerId, roleName))
    }
}