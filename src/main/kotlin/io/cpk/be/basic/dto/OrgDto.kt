package io.cpk.be.basic.dto

import jakarta.validation.constraints.NotEmpty

data class OrgDto(
    val id: Int? = null,

    @field:NotEmpty(message = "Name cannot be empty")
    val name: String,

    val address: String? = null,
    val phone: String? = null,
    val city: String? = null,
    val country: String? = null,
    val notes: String? = null,

    @field:NotEmpty(message = "Org type name cannot be empty")
    val orgTypeName: String,
    
    val orgConfigs: Map<String, Any> = emptyMap()
) 