package com.temquadra.temquadra_api.dto

import java.util.UUID

data class LoginRequest(
    val email: String,
    val passwords: String
)

data class LoginResponse(
    val token: String,
    val userId: UUID,
    val name: String,
    val role: String
)

data class RegisterRequest(
    val name: String,
    val email: String,
    val passwords: String,
    val role: String = "USUARIO"
)
