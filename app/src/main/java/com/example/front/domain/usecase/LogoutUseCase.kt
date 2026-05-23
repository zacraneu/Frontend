package com.example.front.domain.usecase

import com.example.front.data.local.TokenStorage
import com.example.front.domain.repository.AuthRepository
import javax.inject.Inject

class LogoutUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val tokenStorage: TokenStorage
) {
    suspend operator fun invoke() {
        runCatching { authRepository.logout() }
        tokenStorage.clearTokens()
    }
}
