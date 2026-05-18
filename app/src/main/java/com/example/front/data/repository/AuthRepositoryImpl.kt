package com.example.front.data.repository

import com.example.front.data.remote.ApiService
import com.example.front.data.remote.LoginRequest
import com.example.front.data.remote.RefreshTokenRequest
import com.example.front.data.remote.RegisterRequest
import com.example.front.domain.model.AuthResponse
import com.example.front.domain.model.RefreshResponse
import com.example.front.domain.repository.AuthRepository
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val apiService: ApiService
) : AuthRepository {
    override suspend fun register(
        email: String,
        password: String,
        fullName: String
    ): AuthResponse = apiService.register(RegisterRequest(email, password, fullName))

    override suspend fun login(email: String, password: String): AuthResponse =
        apiService.login(LoginRequest(email, password))

    override suspend fun refreshToken(refreshToken: String): RefreshResponse =
        apiService.refreshToken(RefreshTokenRequest(refreshToken))

    override suspend fun logout() {
        apiService.logout()
    }
}
