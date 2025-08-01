package io.cpk.be.basic.repository

import io.cpk.be.basic.entity.Org
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface OrgRepository : JpaRepository<Org, Int> {
    fun findAllByNameContaining(
        name: String,
        pageable: Pageable
    ): Page<Org>
}