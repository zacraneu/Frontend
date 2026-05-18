package com.example.front

import com.example.front.utils.ValidationUtils
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ValidationUtilsTest {
    @Test
    fun emailValidation_acceptsValidEmail() {
        assertTrue(ValidationUtils.isEmailValid("user@example.com"))
    }

    @Test
    fun emailValidation_rejectsInvalidEmail() {
        assertFalse(ValidationUtils.isEmailValid("invalid-email"))
    }

    @Test
    fun passwordValidation_requiresEightCharacters() {
        assertFalse(ValidationUtils.isPasswordValid("1234567"))
        assertTrue(ValidationUtils.isPasswordValid("12345678"))
    }

    @Test
    fun phoneValidation_requiresRussianFormattedPhone() {
        assertTrue(ValidationUtils.isPhoneValid("+7-999-123-45-67"))
        assertFalse(ValidationUtils.isPhoneValid("+79991234567"))
    }
}
