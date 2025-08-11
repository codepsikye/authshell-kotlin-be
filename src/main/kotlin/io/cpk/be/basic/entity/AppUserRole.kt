package io.cpk.be.basic.entity

import jakarta.persistence.*
import java.io.Serializable

@Entity
@Table(name = "app_user_role")
@IdClass(AppUserRole.AppUserRoleId::class)
class AppUserRole(
    @Id
    @Column(name = "user_id")
    val userId: Int = 0,
    
    @Id
    @Column(name = "org_id")
    val orgId: Int = 0,
    
    @Id
    @Column(name = "center_id")
    val centerId: Int = 0,
    
    @Id
    @Column(name = "role_name")
    val roleName: String = "",

    // Relationships
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    val user: AppUser? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "org_id", insertable = false, updatable = false)
    val org: Org? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "center_id", insertable = false, updatable = false)
    val center: Center? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumns(
        JoinColumn(name = "org_id", referencedColumnName = "org_id", insertable = false, updatable = false),
        JoinColumn(name = "role_name", referencedColumnName = "name", insertable = false, updatable = false)
    )
    val role: Role? = null,

) : BaseAuditable() {
    // No-arg constructor required by JPA and the no-arg plugin
    constructor() : this(0, 0, 0, "", null, null, null, null)

    // Add copy method to maintain data class functionality
    fun copy(
        userId: Int = this.userId,
        orgId: Int = this.orgId,
        centerId: Int = this.centerId,
        roleName: String = this.roleName,
        user: AppUser? = this.user,
        org: Org? = this.org,
        center: Center? = this.center,
        role: Role? = this.role
    ): AppUserRole {
        return AppUserRole(userId, orgId, centerId, roleName, user, org, center, role)
    }

    data class AppUserRoleId(
        val userId: Int = 0,
        val orgId: Int = 0,
        val centerId: Int = 0,
        val roleName: String = ""
    ) : Serializable

    // Add equals, hashCode, and toString methods to maintain data class functionality
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is AppUserRole) return false
        if (userId != other.userId) return false
        if (orgId != other.orgId) return false
        if (centerId != other.centerId) return false
        if (roleName != other.roleName) return false
        return true
    }

    override fun hashCode(): Int {
        var result = userId.hashCode()
        result = 31 * result + orgId.hashCode()
        result = 31 * result + centerId.hashCode()
        result = 31 * result + roleName.hashCode()
        return result
    }

    override fun toString(): String {
        return "AppUserRole(userId='$userId', orgId=$orgId, centerId=$centerId, roleName='$roleName')"
    }
}
