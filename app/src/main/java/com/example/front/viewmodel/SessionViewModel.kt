package com.example.front.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.front.data.local.AuthSessionManager
import com.example.front.data.local.TokenStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class SessionViewModel @Inject constructor(
    private val authSessionManager: AuthSessionManager,
    private val tokenStorage: TokenStorage
) : ViewModel() {
    private val _uiState = MutableStateFlow<SessionUiState>(
        SessionUiState.Ready(isLoggedIn = false, role = "USER")
    )
    val uiState: StateFlow<SessionUiState> = _uiState.asStateFlow()

    private val _navigateToLogin = MutableStateFlow(false)
    val navigateToLogin: StateFlow<Boolean> = _navigateToLogin.asStateFlow()

    init {
        viewModelScope.launch {
            val loggedIn = withContext(Dispatchers.IO) {
                authSessionManager.isLoggedIn()
            }
            _uiState.value = SessionUiState.Ready(
                isLoggedIn = loggedIn,
                role = tokenStorage.getRole()
            )
        }
        viewModelScope.launch {
            authSessionManager.sessionExpired.collect {
                _navigateToLogin.value = true
                _uiState.value = SessionUiState.Ready(isLoggedIn = false, role = "USER")
            }
        }
    }

    fun currentRole(): String = tokenStorage.getRole()

    fun onNavigateToLoginHandled() {
        _navigateToLogin.value = false
    }
}

sealed interface SessionUiState {
    data class Ready(val isLoggedIn: Boolean, val role: String) : SessionUiState
}
