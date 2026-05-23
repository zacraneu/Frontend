package com.example.front.data.local

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthSessionManager @Inject constructor(
    private val tokenStorage: TokenStorage
) {
    private val _sessionExpired = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val sessionExpired: SharedFlow<Unit> = _sessionExpired.asSharedFlow()

    fun isLoggedIn(): Boolean = !tokenStorage.getRefreshToken().isNullOrBlank()

    fun notifySessionExpired() {
        tokenStorage.clearTokens()
        _sessionExpired.tryEmit(Unit)
    }
}
