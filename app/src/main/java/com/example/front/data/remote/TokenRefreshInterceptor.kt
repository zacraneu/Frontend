package com.example.front.data.remote

import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class TokenRefreshInterceptor @Inject constructor(
    private val tokenRefreshService: TokenRefreshService
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val path = request.url.encodedPath

        if (!requiresAuthentication(path)) {
            return chain.proceed(request)
        }

        if (tokenRefreshService.refreshSync().not()) {
            tokenRefreshService.handleSessionExpired()
        }

        var response = chain.proceed(request)

        if (response.code == HTTP_UNAUTHORIZED && requiresAuthentication(path)) {
            response.close()
            if (tokenRefreshService.refreshSync()) {
                response = chain.proceed(request)
            } else {
                tokenRefreshService.handleSessionExpired()
                response = chain.proceed(request)
            }
        }

        return response
    }

    private fun requiresAuthentication(path: String): Boolean =
        !path.contains("/auth/login") &&
            !path.contains("/auth/register") &&
            !path.contains("/auth/refresh")

    private companion object {
        const val HTTP_UNAUTHORIZED = 401
    }
}
