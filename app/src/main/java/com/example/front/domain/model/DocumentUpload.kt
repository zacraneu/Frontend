package com.example.front.domain.model

import android.net.Uri
import java.io.File

data class DocumentUpload(
    val uri: Uri,
    val file: File,
    val name: String
)
