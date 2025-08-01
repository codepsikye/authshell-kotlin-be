package io.cpk.be.basic.repository

import io.cpk.be.basic.entity.AppUserRole
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface AppUserRoleRepository : JpaRepository<AppUserRole, AppUserRole.AppUserRoleId> {

    /**
     * Find all centerIds for a given userId
     */
    @Query("SELECT DISTINCT aur.centerId FROM AppUserRole aur WHERE aur.userId = :userId")
    fun findCenterIdsByUserId(userId: String): List<Int>

    /**
     * Check if a user has a unique centerId
     */
    @Query("SELECT CASE WHEN COUNT(DISTINCT aur.centerId) = 1 THEN true ELSE false END FROM AppUserRole aur WHERE aur.userId = :userId")
    fun hasUniqueCenterId(userId: String): Boolean

    /**
     * Get the unique centerId for a user if it exists
     */
    @Query("SELECT aur.centerId FROM AppUserRole aur WHERE aur.userId = :userId GROUP BY aur.centerId HAVING COUNT(DISTINCT aur.centerId) = 1")
    fun getUniqueCenterId(userId: String): Int?

    /**
     * Find all role names for a given userId and centerId
     */
    @Query("SELECT aur.roleName FROM AppUserRole aur WHERE aur.userId = :userId AND aur.centerId = :centerId")
    fun findRoleNamesByUserIdAndCenterId(userId: String, centerId: Int): List<String>

    /**
     * Find all access rights for a given userId and centerId
     */
    @Query("SELECT r.accessRight FROM AppUserRole aur JOIN aur.role r WHERE aur.userId = :userId AND aur.centerId = :centerId")
    fun findAccessRightsByUserIdAndCenterId(userId: String, centerId: Int): List<List<String>>
}