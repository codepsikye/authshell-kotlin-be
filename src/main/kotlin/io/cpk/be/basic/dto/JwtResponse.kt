package io.cpk.be.basic.dto

data class JwtResponse(
    val accessToken: String,
    val refreshToken: String,
    val tokenType: String = "Bearer",
    val expiresIn: Long,
    val user: UserInfo
)

data class UserInfo(
    val id: Int,
    val username: String,
    val fullname: String,
    val email: String,
    val orgId: Int,
    val centerId: Int?,
    val orgAdmin: Boolean,
    val accessRight: List<String>
)
