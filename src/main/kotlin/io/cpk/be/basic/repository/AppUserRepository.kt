package io.cpk.be.basic.repository

import io.cpk.be.basic.entity.AppUser
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface AppUserRepository : JpaRepository<AppUser, String> {
    fun findByUsername(username: String): Optional<AppUser>
    fun findAllByOrgId(orgId: Int, pageable: org.springframework.data.domain.Pageable): Page<AppUser>

    fun findAllByOrgIdAndFullnameContaining(
        orgId: Int,
        fullname: String,
        pageable: Pageable
    ): Page<AppUser>
}
