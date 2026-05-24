package com.example.front.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

class ImageCompression @Inject constructor() {
    fun compress(file: File): File {
        val extension = file.extension.lowercase()
        if (extension !in setOf("jpg", "jpeg", "png")) return file

        val bitmap = BitmapFactory.decodeFile(file.absolutePath) ?: return file
        val outputFile = File(file.parent, "compressed_${file.nameWithoutExtension}.jpg")

        FileOutputStream(outputFile).use { stream ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, JPEG_QUALITY, stream)
        }
        bitmap.recycle()

        if (outputFile.length() < file.length()) {
            if (file.absolutePath != outputFile.absolutePath) {
                file.delete()
            }
            return outputFile
        }

        outputFile.delete()
        return file
    }

    private companion object {
        const val JPEG_QUALITY = 85
    }
}
