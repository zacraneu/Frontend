package com.example.front

import com.example.front.utils.PhoneFormatter
import org.junit.Assert.assertEquals
import org.junit.Test

class PhoneFormatterTest {
    @Test
    fun formatsRussianPhone() {
        assertEquals("+7-999-123-45-67", PhoneFormatter.formatDigits("89991234567"))
    }

    @Test
    fun keepsPartialInputFormatted() {
        assertEquals("+7-999", PhoneFormatter.formatDigits("999"))
    }

    @Test
    fun normalizesLeadingEight() {
        assertEquals("9991234567", PhoneFormatter.normalizeDigits("89991234567"))
    }
}
