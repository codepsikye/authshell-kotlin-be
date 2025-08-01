package io.cpk.be.basic.dto

import jakarta.validation.constraints.NotNull

/**
 * Request to set centerId for a user's token
 */
data class SetCenterRequest(
    @field:NotNull
    val centerId: Int
)