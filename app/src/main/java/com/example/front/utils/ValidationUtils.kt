package com.example.front.utils

object ValidationUtils {
    private val emailRegex = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")
    private val phoneRegex = Regex("^\\+7-\\d{3}-\\d{3}-\\d{2}-\\d{2}$")

    fun isEmailValid(email: String): Boolean = emailRegex.matches(email.trim())

    fun isPasswordValid(password: String): Boolean = password.length >= 8

    fun isPhoneValid(phone: String): Boolean = phoneRegex.matches(phone.trim())

    fun isRequiredTextValid(value: String): Boolean = value.isNotBlank()
}
