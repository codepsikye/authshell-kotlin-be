package io.cpk.be.basic.dto

import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.NotNull

data class AppUserRoleDto(
    @field:NotNull(message = "User ID cannot be null")
    val userId: Int,

    @field:NotNull(message = "Org ID cannot be null")
    val orgId: Int,

    @field:NotNull(message = "Center ID cannot be null")
    val centerId: Int,

    @field:NotEmpty(message = "Role name cannot be empty")
    val roleName: String
) 