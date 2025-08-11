package io.cpk.be.basic.dto

/**
 * Data Transfer Object for AppUser entity.
 * Used for transferring user data between the controller and client.
 */
data class AppUserDto(
    val id: Int,
    val orgId: Int,
    val username: String,
    val fullname: String,
    val title: String? = null,
    val email: String,
    val password: String? = null, // Only used for creation/updates, never returned
    val orgAdmin: Boolean = false,
    val userPrefs: Map<String, Any> = emptyMap()
) {
    // Method to convert userPrefs Map to UserPrefs object
    fun getUserPrefsObject(): UserPrefs {
        return UserPrefs.fromMap(userPrefs)
    }
    
    companion object {
        // Factory method to create AppUserDto from UserPrefs object
        fun fromUserPrefs(
            id: Int,
            orgId: Int,
            username: String,
            fullname: String,
            title: String? = null,
            email: String,
            password: String? = null,
            orgAdmin: Boolean = false,
            userPrefs: UserPrefs
        ): AppUserDto {
            return AppUserDto(
                id = id,
                orgId = orgId,
                username = username,
                fullname = fullname,
                title = title,
                email = email,
                password = password,
                orgAdmin = orgAdmin,
                userPrefs = userPrefs.toMap()
            )
        }
    }
}