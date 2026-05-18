package com.example.front.utils

import java.io.File

object FileUtils {
    fun isAllowedDocument(file: File): Boolean =
        file.extension.lowercase() in setOf("pdf", "jpg", "jpeg", "png")

    fun isFileSizeAllowed(file: File): Boolean =
        file.length() <= Constants.MAX_FILE_SIZE_BYTES
}
