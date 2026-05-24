package com.example.front.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.front.data.local.AuthSessionManager
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
    private val authSessionManager: AuthSessionManager
) : ViewModel() {
    private val _uiState = MutableStateFlow<SessionUiState>(SessionUiState.Ready(isLoggedIn = false))
    val uiState: StateFlow<SessionUiState> = _uiState.asStateFlow()

    private val _navigateToLogin = MutableStateFlow(false)
    val navigateToLogin: StateFlow<Boolean> = _navigateToLogin.asStateFlow()

    init {
        viewModelScope.launch {
            val loggedIn = withContext(Dispatchers.IO) {
                authSessionManager.isLoggedIn()
            }
            _uiState.value = SessionUiState.Ready(isLoggedIn = loggedIn)
        }
        viewModelScope.launch {
            authSessionManager.sessionExpired.collect {
                _navigateToLogin.value = true
                _uiState.value = SessionUiState.Ready(isLoggedIn = false)
            }
        }
    }

    fun onNavigateToLoginHandled() {
        _navigateToLogin.value = false
    }
}

sealed interface SessionUiState {
    data class Ready(val isLoggedIn: Boolean) : SessionUiState
}
