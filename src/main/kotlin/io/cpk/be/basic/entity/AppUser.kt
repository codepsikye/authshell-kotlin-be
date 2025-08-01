package io.cpk.be.basic.entity

import io.cpk.be.basic.dto.UserPrefs
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes

/**
 * Entity representing the app_user table in the database.
 */
@Entity
@Table(name = "app_user")
class AppUser(
    @Id
    val id: String,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "org_id", nullable = false, updatable = false)
    val org: Org? = null,

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
    
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "user_prefs", nullable = false, columnDefinition = "json")
    val userPrefs: UserPrefs = UserPrefs(),
) : BaseAuditable() {
    // Property to maintain compatibility with code that uses orgId
    val orgId: Int
        get() = org?.id ?: 0
    // No-arg constructor required by JPA and the no-arg plugin
    constructor() : this("", null, "", "", null, "", null, false, UserPrefs())

    // Add copy method to maintain data class functionality
    fun copy(
        id: String = this.id,
        org: Org? = this.org,
        username: String = this.username,
        fullname: String = this.fullname,
        title: String? = this.title,
        email: String = this.email,
        password: String? = this.password,
        orgAdmin: Boolean = this.orgAdmin,
        userPrefs: UserPrefs = this.userPrefs,
    ): AppUser {
        return AppUser(id, org, username, fullname, title, email, password, orgAdmin, userPrefs)
    }

    // Add equals, hashCode, and toString methods to maintain data class functionality
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is AppUser) return false
        if (id != other.id) return false
        if (org != other.org) return false
        if (username != other.username) return false
        if (fullname != other.fullname) return false
        if (title != other.title) return false
        if (email != other.email) return false
        if (password != other.password) return false
        if (orgAdmin != other.orgAdmin) return false
        if (userPrefs != other.userPrefs) return false
        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + (org?.hashCode() ?: 0)
        result = 31 * result + username.hashCode()
        result = 31 * result + fullname.hashCode()
        result = 31 * result + (title?.hashCode() ?: 0)
        result = 31 * result + email.hashCode()
        result = 31 * result + (password?.hashCode() ?: 0)
        result = 31 * result + orgAdmin.hashCode()
        result = 31 * result + userPrefs.hashCode()
        return result
    }

    override fun toString(): String {
        return "AppUser(id='$id', org=${org?.id}, username='$username', fullname='$fullname', " +
                "title=$title, email='$email', password=$password, orgAdmin=$orgAdmin, userPrefs=$userPrefs)"
    }
    
    companion object {
        // Factory method to create AppUser from Map<String, Any> for userPrefs
        fun fromUserPrefsMap(
            id: String,
            orgId: Int,
            username: String,
            fullname: String,
            title: String? = null,
            email: String,
            password: String? = null,
            orgAdmin: Boolean = false,
            userPrefs: Map<String, Any> = emptyMap()
        ): AppUser {
            // Create an Org object with just the ID
            val org = if (orgId > 0) Org(id = orgId, name = "", orgTypeName = "DEFAULT", orgConfigs = emptyMap()) else null
            return AppUser(id, org, username, fullname, title, email, password, orgAdmin, UserPrefs.fromMap(userPrefs))
        }
        
        // Factory method to create AppUser from orgId (for backward compatibility)
        fun create(
            id: String,
            orgId: Int,
            username: String,
            fullname: String,
            title: String? = null,
            email: String,
            password: String? = null,
            orgAdmin: Boolean = false,
            userPrefs: UserPrefs = UserPrefs()
        ): AppUser {
            // Create an Org object with just the ID
            val org = if (orgId > 0) Org(id = orgId, name = "", orgTypeName = "DEFAULT", orgConfigs = emptyMap()) else null
            return AppUser(id, org, username, fullname, title, email, password, orgAdmin, userPrefs)
        }
    }
}
