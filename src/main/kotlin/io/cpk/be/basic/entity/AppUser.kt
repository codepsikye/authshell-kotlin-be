package io.cpk.be.basic.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table

/**
 * Entity representing the app_user table in the database.
 */
@Entity
@Table(name = "app_user")
class AppUser(
    @Id
    val id: String,

    @Column(name = "org_id", nullable = false)
    val orgId: Int,

    @Column(nullable = false)
    val username: String,

    @Column(nullable = false)
    val fullname: String,

    @Column
    val title: String? = null,

    @Column(nullable = false)
    val email: String,

    @Column
    val password: String? = null,

    @Column(name = "org_admin", nullable = false)
    val orgAdmin: Boolean = false,
) : BaseAuditable() {
    // No-arg constructor required by JPA and the no-arg plugin
    constructor() : this("", 0, "", "", null, "", null, false)

    // Add copy method to maintain data class functionality
    fun copy(
        id: String = this.id,
        orgId: Int = this.orgId,
        username: String = this.username,
        fullname: String = this.fullname,
        title: String? = this.title,
        email: String = this.email,
        password: String? = this.password,
        orgAdmin: Boolean = this.orgAdmin,
    ): AppUser {
        return AppUser(id, orgId, username, fullname, title, email, password, orgAdmin)
    }

    // Add equals, hashCode, and toString methods to maintain data class functionality
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is AppUser) return false
        if (id != other.id) return false
        if (orgId != other.orgId) return false
        if (username != other.username) return false
        if (fullname != other.fullname) return false
        if (title != other.title) return false
        if (email != other.email) return false
        if (password != other.password) return false
        if (orgAdmin != other.orgAdmin) return false
        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + orgId.hashCode()
        result = 31 * result + username.hashCode()
        result = 31 * result + fullname.hashCode()
        result = 31 * result + (title?.hashCode() ?: 0)
        result = 31 * result + email.hashCode()
        result = 31 * result + (password?.hashCode() ?: 0)
        result = 31 * result + orgAdmin.hashCode()
        return result
    }

    override fun toString(): String {
        return "AppUser(id='$id', orgId=$orgId, username='$username', fullname='$fullname', " +
                "title=$title, email='$email', password=$password, orgAdmin=$orgAdmin)"
    }
}
