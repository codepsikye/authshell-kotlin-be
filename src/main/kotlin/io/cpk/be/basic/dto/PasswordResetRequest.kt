package io.cpk.be.basic.dto

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class PasswordResetRequest(
    @field:NotBlank(message = "Email cannot be blank")
    @field:Email(message = "Email should be valid")
    val email: String
)

data class PasswordResetConfirm(
    @field:NotBlank(message = "Token cannot be blank") val token: String,
    @field:NotBlank(message = "New password cannot be blank")
    @field:Size(min = 8, message = "Password must be at least 8 characters long")
    val newPassword: String
)
