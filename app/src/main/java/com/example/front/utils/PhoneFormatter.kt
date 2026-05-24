package com.example.front.utils

object PhoneFormatter {
    /** Extracts up to 10 national digits from arbitrary user input. */
    fun normalizeDigits(input: String): String {
        val digits = input.filter(Char::isDigit)
        return when {
            digits.startsWith("7") && digits.length > 10 -> digits.substring(1, 11)
            digits.startsWith("8") && digits.length > 10 -> digits.substring(1, 11)
            digits.length > 10 -> digits.takeLast(10)
            else -> digits
        }
    }

    /** Formats national digits as +7-XXX-XXX-XX-XX for API and display. */
    fun formatDigits(digits: String): String {
        val national = normalizeDigits(digits)
        if (national.isEmpty()) return ""

        val builder = StringBuilder("+7")
        val groups = listOf(3, 3, 2, 2)
        var index = 0
        for (size in groups) {
            if (index >= national.length) break
            builder.append('-')
            val end = minOf(index + size, national.length)
            builder.append(national.substring(index, end))
            index = end
        }
        return builder.toString()
    }

    fun format(input: String): String = formatDigits(normalizeDigits(input))

    /** Parses stored phone (+7-... or digits) into national digits. */
    fun parseToDigits(phone: String): String = normalizeDigits(phone)
}
