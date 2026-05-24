package com.example.front.data.local

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.example.front.domain.model.AuthResponse
import com.example.front.domain.model.RefreshResponse
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TokenStorage @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val preferences by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
        val masterKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()
        EncryptedSharedPreferences.create(
            context,
            "secure_auth_tokens",
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    /** Warms up encrypted storage off the main thread during app start. */
    fun warmUp() {
        preferences
    }

    fun saveFromAuthResponse(response: AuthResponse) {
        saveTokens(
            accessToken = response.accessToken,
            refreshToken = response.refreshToken,
            expiresInSeconds = response.expiresIn
        )
        preferences.edit()
            .putString(KEY_USER_ID, response.userId)
            .putString(KEY_EMAIL, response.email)
            .putString(KEY_FULL_NAME, response.fullName)
            .apply()
    }

    fun saveFromRefreshResponse(response: RefreshResponse) {
        saveTokens(
            accessToken = response.accessToken,
            refreshToken = response.refreshToken,
            expiresInSeconds = response.expiresIn
        )
    }

    fun saveTokens(accessToken: String, refreshToken: String, expiresInSeconds: Long) {
        val expiresAt = System.currentTimeMillis() + expiresInSeconds * 1000
        preferences.edit()
            .putString(KEY_ACCESS_TOKEN, accessToken)
            .putString(KEY_REFRESH_TOKEN, refreshToken)
            .putLong(KEY_ACCESS_EXPIRES_AT, expiresAt)
            .apply()
    }

    fun getAccessToken(): String? = preferences.getString(KEY_ACCESS_TOKEN, null)

    fun getRefreshToken(): String? = preferences.getString(KEY_REFRESH_TOKEN, null)

    fun getUserId(): String? = preferences.getString(KEY_USER_ID, null)

    fun getEmail(): String? = preferences.getString(KEY_EMAIL, null)

    fun getFullName(): String? = preferences.getString(KEY_FULL_NAME, null)

    fun isAccessTokenExpired(): Boolean {
        val accessToken = getAccessToken()
        if (accessToken.isNullOrBlank()) return true

        val expiresAt = preferences.getLong(KEY_ACCESS_EXPIRES_AT, 0L)
        if (expiresAt == 0L) return false

        return System.currentTimeMillis() >= expiresAt - EXPIRY_BUFFER_MS
    }

    fun clearTokens() {
        preferences.edit().clear().apply()
    }

    private companion object {
        const val KEY_ACCESS_TOKEN = "access_token"
        const val KEY_REFRESH_TOKEN = "refresh_token"
        const val KEY_ACCESS_EXPIRES_AT = "access_expires_at"
        const val KEY_USER_ID = "user_id"
        const val KEY_EMAIL = "email"
        const val KEY_FULL_NAME = "full_name"
        const val EXPIRY_BUFFER_MS = 30_000L
    }
}
