package io.cpk.be.tasks.dto

import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.NotNull

data class TaskDto(
    val id: Long? = null,

    @field:NotEmpty(message = "Subject cannot be empty")
    val subject: String?,

    val body: String? = null,
    val status: String? = "pending",

    @field:NotNull(message = "Center ID cannot be null")
    val centerId: Int?
) 