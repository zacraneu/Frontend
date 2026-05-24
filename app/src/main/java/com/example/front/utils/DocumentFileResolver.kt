package com.example.front.utils

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DocumentFileResolver @Inject constructor(
    @ApplicationContext private val context: Context
) {
    fun resolve(uri: Uri): ResolvedDocument {
        val displayName = queryDisplayName(uri)
        val extension = displayName.substringAfterLast('.', "").lowercase()
        val tempFile = File(
            context.cacheDir,
            "upload_${System.currentTimeMillis()}.$extension"
        )

        context.contentResolver.openInputStream(uri)?.use { input ->
            tempFile.outputStream().use { output -> input.copyTo(output) }
        } ?: throw IOException("Не удалось прочитать файл")

        return ResolvedDocument(file = tempFile, displayName = displayName)
    }

    fun queryDisplayName(uri: Uri): String {
        context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            if (cursor.moveToFirst() && nameIndex >= 0) {
                return cursor.getString(nameIndex)
            }
        }
        return "document"
    }

    data class ResolvedDocument(
        val file: File,
        val displayName: String
    )
}
