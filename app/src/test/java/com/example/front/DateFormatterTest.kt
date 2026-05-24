package com.example.front

import com.example.front.utils.DateFormatter
import org.junit.Assert.assertEquals
import org.junit.Test

class DateFormatterTest {
    @Test
    fun shortId_truncatesLongUuid() {
        assertEquals("APP-UUID", DateFormatter.shortId("app-uuid-1234-5678").take(8))
    }

    @Test
    fun formatIso_parsesUtcTimestamp() {
        val formatted = DateFormatter.formatIso("2025-05-18T10:30:00Z")
        assert(formatted.contains("2025"))
        assert(formatted.contains("10:30") || formatted.contains("13:30"))
    }
}
