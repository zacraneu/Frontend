package com.example.front.domain.usecase

import com.example.front.data.local.TokenStorage
import com.example.front.domain.model.AuthResponse
import com.example.front.domain.repository.AuthRepository
import com.example.front.utils.ValidationUtils
import javax.inject.Inject

class RegisterUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val tokenStorage: TokenStorage
) {
    suspend operator fun invoke(
        email: String,
        password: String,
        fullName: String
    ): AuthResponse {
        require(ValidationUtils.isRequiredTextValid(fullName)) { "ФИО не может быть пустым" }
        require(ValidationUtils.isEmailValid(email)) { "Некорректный email" }
        require(ValidationUtils.isPasswordValid(password)) { "Пароль должен быть не менее 8 символов" }

        return authRepository.register(email.trim(), password, fullName.trim()).also { response ->
            tokenStorage.saveFromAuthResponse(response)
        }
    }
}
