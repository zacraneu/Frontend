package com.example.front.data.repository

import com.example.front.data.di.AuthNetwork
import com.example.front.data.di.Authenticated
import com.example.front.data.remote.ApiService
import com.example.front.data.remote.LoginRequest
import com.example.front.data.remote.RefreshTokenRequest
import com.example.front.data.remote.RegisterRequest
import com.example.front.domain.model.AuthResponse
import com.example.front.domain.model.RefreshResponse
import com.example.front.domain.repository.AuthRepository
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    @AuthNetwork private val authApi: ApiService,
    @Authenticated private val authenticatedApi: ApiService
) : AuthRepository {
    override suspend fun register(
        email: String,
        password: String,
        fullName: String
    ): AuthResponse = authApi.register(RegisterRequest(email, password, fullName))

    override suspend fun login(email: String, password: String): AuthResponse =
        authApi.login(LoginRequest(email, password))

    override suspend fun refreshToken(refreshToken: String): RefreshResponse =
        authApi.refreshToken(RefreshTokenRequest(refreshToken))

    override suspend fun logout() {
        authenticatedApi.logout()
    }
}
