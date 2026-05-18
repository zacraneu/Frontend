package com.example.front.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Document(
    val id: String,
    val applicationId: String? = null,
    val filename: String,
    val originalFilename: String? = null,
    val fileSize: Long,
    val mimeType: String? = null,
    val uploadedAt: String
)
