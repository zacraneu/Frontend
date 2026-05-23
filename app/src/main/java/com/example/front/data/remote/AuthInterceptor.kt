package com.example.front.data.remote

import com.example.front.data.local.TokenStorage
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class AuthInterceptor @Inject constructor(
    private val tokenStorage: TokenStorage
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val path = request.url.encodedPath

        if (!requiresAuthorization(path)) {
            return chain.proceed(request)
        }

        val token = tokenStorage.getAccessToken()
        val authenticatedRequest = if (token.isNullOrBlank()) {
            request
        } else {
            request.newBuilder()
                .header("Authorization", "Bearer $token")
                .build()
        }

        return chain.proceed(authenticatedRequest)
    }

    private fun requiresAuthorization(path: String): Boolean =
        !path.contains("/auth/login") &&
            !path.contains("/auth/register") &&
            !path.contains("/auth/refresh")
}
