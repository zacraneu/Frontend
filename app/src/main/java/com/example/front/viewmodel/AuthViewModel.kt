package com.example.front.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.front.domain.model.AuthResponse
import com.example.front.domain.usecase.LoginUseCase
import com.example.front.domain.usecase.RegisterUseCase
import com.example.front.utils.ApiErrorMapper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val registerUseCase: RegisterUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    private val _isRegisterMode = MutableStateFlow(false)
    val isRegisterMode: StateFlow<Boolean> = _isRegisterMode.asStateFlow()

    fun setRegisterMode(enabled: Boolean) {
        _isRegisterMode.value = enabled
        _uiState.value = AuthUiState.Idle
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            _uiState.value = runCatching {
                loginUseCase(email, password)
            }.fold(
                onSuccess = AuthUiState::Success,
                onFailure = { AuthUiState.Error(ApiErrorMapper.message(it)) }
            )
        }
    }

    fun register(email: String, password: String, fullName: String) {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            _uiState.value = runCatching {
                registerUseCase(email, password, fullName)
            }.fold(
                onSuccess = AuthUiState::Success,
                onFailure = { AuthUiState.Error(ApiErrorMapper.message(it)) }
            )
        }
    }

    fun clearError() {
        if (_uiState.value is AuthUiState.Error) {
            _uiState.value = AuthUiState.Idle
        }
    }
}

sealed interface AuthUiState {
    data object Idle : AuthUiState
    data object Loading : AuthUiState
    data class Success(val response: AuthResponse) : AuthUiState
    data class Error(val message: String) : AuthUiState
    data object TokenExpired : AuthUiState
}
