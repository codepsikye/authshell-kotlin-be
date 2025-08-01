package io.cpk.be.basic.entity

import jakarta.persistence.*
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes
import java.io.Serializable
import java.time.LocalDateTime

@Entity
@Table(name = "role")
@IdClass(Role.RoleId::class)
class Role(
    @Id @Column(name = "org_id", nullable = false) val orgId: Int,
    @Id @Column(name = "name", nullable = false) val name: String,
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "access_right", nullable = false, columnDefinition = "json")
    var accessRight: List<String> = mutableListOf(),

    // Relationships
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "org_id", insertable = false, updatable = false)
    val org: Org? = null
) : BaseAuditable() {
    // No-arg constructor required by JPA and the no-arg plugin
    constructor() : this(0, "", emptyList(), null)

    // Add copy method to maintain data class functionality
    fun copy(
        orgId: Int = this.orgId,
        name: String = this.name,
        accessRight: List<String> = this.accessRight,
        org: Org? = this.org,
        createdAt: LocalDateTime = this.createdAt,
        updatedAt: LocalDateTime = this.updatedAt
    ): Role {
        return Role(orgId, name, accessRight, org)
    }

    data class RoleId(val orgId: Int = 0, val name: String = "") : Serializable

    // Add equals, hashCode, and toString methods to maintain data class functionality
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Role) return false
        if (orgId != other.orgId) return false
        if (name != other.name) return false
        if (accessRight != other.accessRight) return false
        return true
    }

    override fun hashCode(): Int {
        var result = orgId.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + accessRight.hashCode()
        return result
    }

    override fun toString(): String {
        return "Role(orgId=$orgId, name='$name', accessRight=$accessRight)"
    }
}
