package com.example.front.data.remote

import com.example.front.data.di.AuthNetwork
import com.example.front.data.local.AuthSessionManager
import com.example.front.data.local.TokenStorage
import kotlinx.coroutines.runBlocking
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TokenRefreshService @Inject constructor(
    @AuthNetwork private val authApi: ApiService,
    private val tokenStorage: TokenStorage,
    private val authSessionManager: AuthSessionManager
) {
    private val refreshLock = Any()

    /**
     * Refreshes the access token synchronously. Returns false if refresh is impossible.
     */
    fun refreshSync(): Boolean = synchronized(refreshLock) {
        val refreshToken = tokenStorage.getRefreshToken() ?: return false
        if (!tokenStorage.isAccessTokenExpired()) return true

        return runBlocking {
            runCatching {
                val response = authApi.refreshToken(RefreshTokenRequest(refreshToken))
                tokenStorage.saveFromRefreshResponse(response)
                true
            }.getOrDefault(false)
        }
    }

    fun handleSessionExpired() {
        authSessionManager.notifySessionExpired()
    }
}
