package com.example.front.ui.components

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import com.example.front.utils.PhoneFormatter

class PhoneVisualTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val digits = text.text
        val formatted = PhoneFormatter.formatDigits(digits)
        return TransformedText(
            text = AnnotatedString(formatted),
            offsetMapping = RussianPhoneOffsetMapping(digits, formatted)
        )
    }
}

private class RussianPhoneOffsetMapping(
    private val digits: String,
    private val formatted: String
) : OffsetMapping {
    override fun originalToTransformed(offset: Int): Int {
        if (offset <= 0) return 0
        if (offset >= digits.length) return formatted.length
        val partial = PhoneFormatter.formatDigits(digits.substring(0, offset))
        return partial.length
    }

    override fun transformedToOriginal(offset: Int): Int {
        if (offset <= 0) return 0
        if (offset >= formatted.length) return digits.length
        var count = 0
        for (i in 0 until offset.coerceAtMost(formatted.length)) {
            if (formatted[i].isDigit()) count++
        }
        return count.coerceIn(0, digits.length)
    }
}
