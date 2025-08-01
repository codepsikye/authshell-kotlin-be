package io.cpk.be.basic.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

/**
 * Data Transfer Object for Center entity. Used for transferring center data between the controller
 * and client.
 */
data class CenterDto(
    val id: Int? = null,
    @field:NotBlank(message = "Name cannot be blank") val name: String,
    val address: String? = null,
    val phone: String? = null,
    @field:NotNull(message = "Organization ID is required") val orgId: Int
)
