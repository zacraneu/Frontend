package com.example.front.domain.repository

import com.example.front.domain.model.AuthResponse
import com.example.front.domain.model.RefreshResponse

interface AuthRepository {
    suspend fun register(email: String, password: String, fullName: String): AuthResponse
    suspend fun login(email: String, password: String): AuthResponse
    suspend fun refreshToken(refreshToken: String): RefreshResponse
    suspend fun logout()
}
