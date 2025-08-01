package io.cpk.be.basic.dto

import jakarta.validation.constraints.NotEmpty

data class OrgTypeDto(
    @field:NotEmpty(message = "Name cannot be empty")
    val name: String,

    val accessRight: List<String> = emptyList(),

    val orgConfigs: OrgConfig
) 