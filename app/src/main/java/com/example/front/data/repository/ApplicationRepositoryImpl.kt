package com.example.front.data.repository

import com.example.front.data.di.Authenticated
import com.example.front.data.remote.ApiService
import com.example.front.domain.model.ApplicationDetail
import com.example.front.domain.model.ApplicationMutationResponse
import com.example.front.domain.model.ApplicationsPage
import com.example.front.domain.repository.ApplicationRepository
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import javax.inject.Inject

class ApplicationRepositoryImpl @Inject constructor(
    @Authenticated private val apiService: ApiService
) : ApplicationRepository {
    override suspend fun getApplications(page: Int, size: Int): ApplicationsPage =
        apiService.getApplications(page = page, size = size)

    override suspend fun getApplication(applicationId: String): ApplicationDetail =
        apiService.getApplication(applicationId)

    override suspend fun createApplication(
        fullName: String,
        email: String,
        phone: String,
        submissionReason: String,
        additionalInfo: String?,
        documents: List<File>
    ): ApplicationMutationResponse = apiService.createApplication(
        fields = buildFields(fullName, email, phone, submissionReason, additionalInfo),
        documents = buildDocumentParts(documents)
    )

    override suspend fun updateApplication(
        applicationId: String,
        fullName: String,
        email: String,
        phone: String,
        submissionReason: String,
        additionalInfo: String?,
        documents: List<File>
    ): ApplicationMutationResponse = apiService.updateApplication(
        applicationId = applicationId,
        fields = buildFields(fullName, email, phone, submissionReason, additionalInfo),
        documents = buildDocumentParts(documents)
    )

    private fun buildFields(
        fullName: String,
        email: String,
        phone: String,
        submissionReason: String,
        additionalInfo: String?
    ): Map<String, RequestBody> = buildMap {
        put("fullName", fullName.asTextRequestBody())
        put("email", email.asTextRequestBody())
        put("phone", phone.asTextRequestBody())
        put("submissionReason", submissionReason.asTextRequestBody())
        additionalInfo?.takeIf { it.isNotBlank() }?.let {
            put("additionalInfo", it.asTextRequestBody())
        }
    }

    private fun buildDocumentParts(documents: List<File>): List<MultipartBody.Part> =
        documents.map { file ->
            val mediaType = when (file.extension.lowercase()) {
                "pdf" -> "application/pdf"
                "jpg", "jpeg" -> "image/jpeg"
                "png" -> "image/png"
                else -> "application/octet-stream"
            }.toMediaTypeOrNull()

            MultipartBody.Part.createFormData(
                name = "documents",
                filename = file.name,
                body = file.asRequestBody(mediaType)
            )
        }

    private fun String.asTextRequestBody(): RequestBody =
        toRequestBody("text/plain".toMediaTypeOrNull())
}
