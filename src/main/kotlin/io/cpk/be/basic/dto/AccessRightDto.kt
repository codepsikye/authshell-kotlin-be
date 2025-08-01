package io.cpk.be.basic.dto

import jakarta.validation.constraints.NotEmpty

data class AccessRightDto(@field:NotEmpty(message = "Name cannot be empty") val name: String)
