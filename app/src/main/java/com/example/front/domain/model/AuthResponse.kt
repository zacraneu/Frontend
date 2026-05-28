package com.example.front.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class AuthResponse(
    val userId: String,
    val email: String,
    val fullName: String? = null,
    val role: String = "USER",
    val accessToken: String,
    val refreshToken: String,
    val expiresIn: Long
)

@Serializable
data class RefreshResponse(
    val accessToken: String,
    val refreshToken: String,
    val expiresIn: Long
)
