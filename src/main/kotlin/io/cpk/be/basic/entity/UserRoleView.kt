package io.cpk.be.basic.entity

import jakarta.persistence.*
import org.hibernate.annotations.Immutable
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes
import java.io.Serializable

@Entity
@Immutable
@Table(name = "user_role_view")
@IdClass(UserRoleView.UserRoleViewId::class)
data class UserRoleView(
    @Id
    @Column(name = "org_id", nullable = false)
    val orgId: Int,

    @Id
    @Column(name = "center_id", nullable = false)
    val centerId: Int,

    @Id
    @Column(name = "role_name", nullable = false)
    val roleName: String,

    @Column(name = "fullname", nullable = false)
    val fullname: String,

    @Column(name = "org_name", nullable = false)
    val orgName: String,

    @Column(name = "center_name", nullable = false)
    val centerName: String,

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "access_right", nullable = false, columnDefinition = "json")
    val accessRight: List<String> = emptyList()
) {
    data class UserRoleViewId(
        val orgId: Int = 0,
        val centerId: Int = 0,
        val roleName: String = ""
    ) : Serializable
} 