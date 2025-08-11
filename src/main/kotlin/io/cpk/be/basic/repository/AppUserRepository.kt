package io.cpk.be.basic.repository

import io.cpk.be.basic.entity.AppUser
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface AppUserRepository : JpaRepository<AppUser, Int> {
    fun findByUsername(username: String): Optional<AppUser>
    
    @Query("SELECT a FROM AppUser a WHERE a.org.id = :orgId")
    fun findAllByOrgId(orgId: Int, pageable: org.springframework.data.domain.Pageable): Page<AppUser>

    @Query("SELECT a FROM AppUser a WHERE a.org.id = :orgId AND a.fullname LIKE %:fullname%")
    fun findAllByOrgIdAndFullnameContaining(
        orgId: Int,
        fullname: String,
        pageable: Pageable
    ): Page<AppUser>
}
