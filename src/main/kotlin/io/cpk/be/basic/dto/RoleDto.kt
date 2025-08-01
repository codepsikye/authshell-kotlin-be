package io.cpk.be.basic.dto

import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.NotNull

data class RoleDto(
    @field:NotNull(message = "Org ID cannot be null")
    val orgId: Int,

    @field:NotEmpty(message = "Name cannot be empty")
    val name: String,

    val accessRight: List<String> = emptyList()
) 