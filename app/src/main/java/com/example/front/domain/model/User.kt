package com.example.front.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val userId: String,
    val email: String,
    val fullName: String,
    val phone: String? = null,
    val registeredAt: String? = null,
    val isVerified: Boolean = false
)
