package io.cpk.be.basic.repository

import io.cpk.be.basic.entity.Center
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface CenterRepository : JpaRepository<Center, Int> {
    @Query("SELECT c FROM Center c WHERE c.org.id = :orgId")
    fun findAllByOrgId(@Param("orgId") orgId: Int, pageable: Pageable): Page<Center>

    @Query("SELECT c FROM Center c WHERE c.org.id = :orgId AND LOWER(c.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    fun findAllByOrgIdAndNameContaining(
        @Param("orgId") orgId: Int,
        @Param("name") name: String,
        pageable: Pageable
    ): Page<Center>
}