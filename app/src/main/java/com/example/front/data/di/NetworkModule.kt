package com.example.front.data.di

import com.example.front.data.remote.ApiService
import com.example.front.data.remote.AuthInterceptor
import com.example.front.data.remote.TokenRefreshInterceptor
import com.example.front.utils.Constants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    @Provides
    @Singleton
    fun provideJson(): Json = Json {
        ignoreUnknownKeys = true
        explicitNulls = false
    }

    @Provides
    @Singleton
    fun provideLoggingInterceptor(): HttpLoggingInterceptor =
        HttpLoggingInterceptor().apply {
            // Never log bodies: passwords and tokens may appear in request/response payloads.
            level = HttpLoggingInterceptor.Level.BASIC
        }

    @Provides
    @Singleton
    @AuthNetwork
    fun provideAuthOkHttpClient(
        loggingInterceptor: HttpLoggingInterceptor
    ): OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .build()

    @Provides
    @Singleton
    @Authenticated
    fun provideAuthenticatedOkHttpClient(
        tokenRefreshInterceptor: TokenRefreshInterceptor,
        authInterceptor: AuthInterceptor,
        loggingInterceptor: HttpLoggingInterceptor
    ): OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(tokenRefreshInterceptor)
        .addInterceptor(authInterceptor)
        .addInterceptor(loggingInterceptor)
        .build()

    @Provides
    @Singleton
    @AuthNetwork
    fun provideAuthRetrofit(
        @AuthNetwork client: OkHttpClient,
        json: Json
    ): Retrofit = buildRetrofit(client, json)

    @Provides
    @Singleton
    fun provideAuthenticatedRetrofit(
        @Authenticated client: OkHttpClient,
        json: Json
    ): Retrofit = buildRetrofit(client, json)

    @Provides
    @Singleton
    @AuthNetwork
    fun provideAuthApiService(@AuthNetwork retrofit: Retrofit): ApiService =
        retrofit.create(ApiService::class.java)

    @Provides
    @Singleton
    @Authenticated
    fun provideAuthenticatedApiService(retrofit: Retrofit): ApiService =
        retrofit.create(ApiService::class.java)

    private fun buildRetrofit(client: OkHttpClient, json: Json): Retrofit =
        Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .client(client)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
}
