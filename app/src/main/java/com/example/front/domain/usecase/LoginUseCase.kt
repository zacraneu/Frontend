package com.example.front.domain.usecase

import com.example.front.data.local.TokenStorage
import com.example.front.domain.model.AuthResponse
import com.example.front.domain.repository.AuthRepository
import com.example.front.utils.ValidationUtils
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val tokenStorage: TokenStorage
) {
    suspend operator fun invoke(email: String, password: String): AuthResponse {
        require(ValidationUtils.isEmailValid(email)) { "Некорректный email" }
        require(ValidationUtils.isPasswordValid(password)) { "Пароль должен быть не менее 8 символов" }

        return authRepository.login(email.trim(), password).also { response ->
            tokenStorage.saveTokens(response.accessToken, response.refreshToken)
        }
    }
}
