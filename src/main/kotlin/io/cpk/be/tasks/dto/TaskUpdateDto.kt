package io.cpk.be.tasks.dto

import jakarta.validation.constraints.NotNull

data class TaskUpdateDto(
    val id: Long? = null,

    @field:NotNull(message = "Task ID cannot be null")
    val taskId: Long?,

    val body: String? = null,

    val status: String?
) 