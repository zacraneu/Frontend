package com.example.front.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.front.data.local.AuthSessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SessionViewModel @Inject constructor(
    authSessionManager: AuthSessionManager
) : ViewModel() {
    val isLoggedIn: Boolean = authSessionManager.isLoggedIn()

    private val _navigateToLogin = MutableStateFlow(false)
    val navigateToLogin: StateFlow<Boolean> = _navigateToLogin.asStateFlow()

    init {
        viewModelScope.launch {
            authSessionManager.sessionExpired.collect {
                _navigateToLogin.value = true
            }
        }
    }

    fun onNavigateToLoginHandled() {
        _navigateToLogin.value = false
    }
}
