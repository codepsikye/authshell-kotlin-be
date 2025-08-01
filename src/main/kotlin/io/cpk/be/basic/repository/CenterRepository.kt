package io.cpk.be.basic.repository

import io.cpk.be.basic.entity.Center
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface CenterRepository : JpaRepository<Center, Int> {
    fun findAllByOrgId(orgId: Int, pageable: Pageable): Page<Center>

    fun findAllByOrgIdAndNameContaining(
        orgId: Int,
        name: String,
        pageable: Pageable
    ): Page<Center>
}