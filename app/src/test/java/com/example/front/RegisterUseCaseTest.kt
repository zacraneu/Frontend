package com.example.front

import com.example.front.data.local.TokenStorage
import com.example.front.domain.model.AuthResponse
import com.example.front.domain.repository.AuthRepository
import com.example.front.domain.usecase.RegisterUseCase
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class RegisterUseCaseTest {
    private val authRepository: AuthRepository = mock()
    private val tokenStorage: TokenStorage = mock()
    private val registerUseCase = RegisterUseCase(authRepository, tokenStorage)

    @Test
    fun register_rejectsInvalidEmail() {
        assertThrows(IllegalArgumentException::class.java) {
            runBlocking {
                registerUseCase("bad-email", "password123", "Ivan Petrov")
            }
        }
    }

    @Test
    fun register_savesTokensOnSuccess() = runBlocking {
        val response = AuthResponse(
            userId = "id-1",
            email = "user@example.com",
            fullName = "Ivan Petrov",
            accessToken = "access",
            refreshToken = "refresh",
            expiresIn = 3600
        )
        whenever(authRepository.register(any(), any(), any())).thenReturn(response)

        val result = registerUseCase("user@example.com", "password123", "Ivan Petrov")

        assertEquals(response, result)
        verify(tokenStorage).saveFromAuthResponse(response)
    }
}
