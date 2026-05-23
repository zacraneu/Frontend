package com.example.front.utils

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import retrofit2.HttpException
import java.io.IOException

object ApiErrorMapper {
    private val json = Json { ignoreUnknownKeys = true }

    fun message(throwable: Throwable): String = when (throwable) {
        is IOException -> "Нет подключения к сети. Проверьте интернет и попробуйте снова."
        is HttpException -> parseHttpException(throwable)
        else -> throwable.message ?: "Произошла неизвестная ошибка"
    }

    private fun parseHttpException(exception: HttpException): String {
        val fallback = when (exception.code()) {
            400 -> "Некорректные данные запроса"
            401 -> "Неверный email или пароль"
            403 -> "Доступ запрещён"
            404 -> "Ресурс не найден"
            409 -> "Пользователь с таким email уже существует"
            in 500..599 -> "Ошибка сервера. Попробуйте позже"
            else -> "Ошибка запроса (${exception.code()})"
        }

        val body = exception.response()?.errorBody()?.string() ?: return fallback
        return runCatching {
            val apiError = json.decodeFromString<ApiErrorBody>(body)
            apiError.message ?: apiError.error ?: fallback
        }.getOrDefault(fallback)
    }

    @Serializable
    private data class ApiErrorBody(
        val message: String? = null,
        val error: String? = null
    )
}
