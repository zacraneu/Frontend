package com.example.front.utils

import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

object DateFormatter {
    private val displayFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm", Locale("ru"))

    fun formatIso(iso: String): String = runCatching {
        val instant = Instant.parse(iso)
        displayFormatter.format(instant.atZone(ZoneId.systemDefault()))
    }.getOrDefault(iso)

    fun shortId(id: String): String =
        if (id.length <= 8) id else id.take(8).uppercase()
}
