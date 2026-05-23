package com.example.front.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.front.data.local.TokenStorage
import com.example.front.domain.model.User
import com.example.front.domain.repository.UserRepository
import com.example.front.domain.usecase.LogoutUseCase
import com.example.front.utils.ApiErrorMapper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val logoutUseCase: LogoutUseCase,
    private val tokenStorage: TokenStorage
) : ViewModel() {
    private val _uiState = MutableStateFlow<ProfileUiState>(ProfileUiState.Loading)
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        loadProfile()
    }

    fun loadProfile() {
        viewModelScope.launch {
            _uiState.value = ProfileUiState.Loading
            _uiState.value = runCatching {
                userRepository.getProfile()
            }.fold(
                onSuccess = ProfileUiState::Success,
                onFailure = {
                    ProfileUiState.Error(
                        message = ApiErrorMapper.message(it),
                        cachedUser = cachedUserFromStorage()
                    )
                }
            )
        }
    }

    fun logout(onLoggedOut: () -> Unit) {
        viewModelScope.launch {
            _uiState.value = ProfileUiState.Loading
            logoutUseCase()
            onLoggedOut()
        }
    }

    private fun cachedUserFromStorage(): User? {
        val userId = tokenStorage.getUserId() ?: return null
        val email = tokenStorage.getEmail() ?: return null
        return User(
            userId = userId,
            email = email,
            fullName = tokenStorage.getFullName().orEmpty()
        )
    }
}

sealed interface ProfileUiState {
    data object Loading : ProfileUiState
    data class Success(val user: User) : ProfileUiState
    data class Error(val message: String, val cachedUser: User? = null) : ProfileUiState
}
