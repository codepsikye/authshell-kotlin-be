package io.cpk.be.basic.repository

import io.cpk.be.basic.entity.OrgType
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface OrgTypeRepository : JpaRepository<OrgType, String> {
    fun findAllByNameContaining(
        name: String,
        pageable: Pageable
    ): Page<OrgType>
}