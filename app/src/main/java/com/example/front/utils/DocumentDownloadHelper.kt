package com.example.front.utils

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DocumentDownloadHelper @Inject constructor(
    @ApplicationContext private val context: Context
) {
    fun save(applicationId: String, fileName: String, bytes: ByteArray): File {
        val directory = File(context.cacheDir, "downloads/$applicationId").apply { mkdirs() }
        val safeName = fileName.replace(Regex("[^a-zA-Z0-9._-]"), "_")
        val file = File(directory, safeName)
        file.writeBytes(bytes)
        return file
    }
}
