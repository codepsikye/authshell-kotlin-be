package io.cpk.be.basic.service

import io.cpk.be.basic.dto.RoleDto
import io.cpk.be.basic.entity.Role
import io.cpk.be.basic.mapper.RoleMapper
import io.cpk.be.basic.repository.RoleRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class RoleService(
    private val roleRepository: RoleRepository,
    private val roleMapper: RoleMapper
) {
    fun create(roleDto: RoleDto): RoleDto {
        val role = roleMapper.toEntity(roleDto)
        val savedRole = roleRepository.save(role)
        return roleMapper.toDto(savedRole)
    }

    @Transactional(readOnly = true)
    fun findAll(): List<RoleDto> {
        return roleRepository.findAll().map(roleMapper::toDto)
    }

    @Transactional(readOnly = true)
    fun findAll(pageable: Pageable): Page<RoleDto> {
        val rolePage = roleRepository.findAll(pageable)
        val roleDtos = rolePage.content.map(roleMapper::toDto)
        return PageImpl(roleDtos, pageable, rolePage.totalElements)
    }

    @Transactional(readOnly = true)
    fun findAll(orgId: Int, pageable: Pageable): Page<RoleDto> {
        val rolePage = roleRepository.findAllByOrgId(orgId, pageable)
        val roleDtos = rolePage.content.map(roleMapper::toDto)
        return PageImpl(roleDtos, pageable, rolePage.totalElements)
    }

    @Transactional(readOnly = true)
    fun findAll(orgId: Int, name: String?, pageable: Pageable): Page<RoleDto> {
        val rolePage = roleRepository.findAllByOrgIdAndNameContaining(orgId, name.orEmpty(), pageable)
        val roleDtos = rolePage.content.map(roleMapper::toDto)
        return PageImpl(roleDtos, pageable, rolePage.totalElements)
    }

    @Transactional(readOnly = true)
    fun findById(orgId: Int, name: String): RoleDto? {
        return roleRepository.findById(Role.RoleId(orgId, name)).map(roleMapper::toDto).orElse(null)
    }

    fun update(orgId: Int, name: String, roleDto: RoleDto): RoleDto {
        val existingRole =
            roleRepository.findById(Role.RoleId(orgId, name)).orElseThrow { RuntimeException("Role not found") }
        val updatedRole = roleMapper.toEntity(roleDto).copy(orgId = existingRole.orgId, name = existingRole.name)
        return roleMapper.toDto(roleRepository.save(updatedRole))
    }

    fun delete(orgId: Int, name: String) {
        roleRepository.deleteById(Role.RoleId(orgId, name))
    }
}
