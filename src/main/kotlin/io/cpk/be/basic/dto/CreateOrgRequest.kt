package io.cpk.be.basic.dto

import jakarta.validation.Valid
import jakarta.validation.constraints.NotNull

/**
 * Data Transfer Object for creating an organization with a center and admin user.
 * This DTO is used by the create_org endpoint to create an organization, a center in that organization,
 * and a user with admin role and all access rights.
 */
data class CreateOrgRequest(
    @field:NotNull(message = "Organization data is required")
    @field:Valid
    val orgDto: OrgDto,

    @field:NotNull(message = "Center data is required")
    @field:Valid
    val centerDto: CenterDto,

    @field:NotNull(message = "User data is required")
    @field:Valid
    val userDto: AppUserDto
)