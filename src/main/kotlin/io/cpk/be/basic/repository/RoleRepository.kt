package io.cpk.be.basic.repository

import io.cpk.be.basic.entity.Role
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface RoleRepository : JpaRepository<Role, Role.RoleId> {
    fun findAllByOrgId(orgId: Int, pageable: Pageable): Page<Role>

    fun findAllByOrgIdAndNameContaining(
        orgId: Int,
        name: String,
        pageable: Pageable
    ): Page<Role>
}