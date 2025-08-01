package io.cpk.be.basic.dto

/**
 * Data Transfer Object for AppUser entity.
 * Used for transferring user data between the controller and client.
 */
data class AppUserDto(
    val id: String,
    val orgId: Int,
    val username: String,
    val fullname: String,
    val title: String? = null,
    val email: String,
    val password: String? = null, // Only used for creation/updates, never returned
    val orgAdmin: Boolean = false
)