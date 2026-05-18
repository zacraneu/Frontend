package com.example.front.domain.model

import kotlinx.serialization.Serializable

@Serializable
enum class ApplicationStatus {
    NEW,
    REVIEWING,
    APPROVED,
    REJECTED,
    RETURNED
}

@Serializable
data class ApplicationSummary(
    val id: String,
    val userId: String,
    val status: ApplicationStatus,
    val createdAt: String,
    val updatedAt: String,
    val documents: List<Document> = emptyList()
)

@Serializable
data class ApplicationDetail(
    val id: String,
    val userId: String,
    val fullName: String,
    val email: String,
    val phone: String,
    val status: ApplicationStatus,
    val submissionReason: String,
    val additionalInfo: String? = null,
    val createdAt: String,
    val updatedAt: String,
    val reviewedAt: String? = null,
    val rejectionReason: String? = null,
    val documents: List<Document> = emptyList()
)

@Serializable
data class ApplicationsPage(
    val content: List<ApplicationSummary>,
    val totalElements: Int,
    val totalPages: Int,
    val currentPage: Int
)

@Serializable
data class ApplicationMutationResponse(
    val id: String,
    val userId: String? = null,
    val status: ApplicationStatus,
    val createdAt: String? = null,
    val updatedAt: String? = null,
    val message: String
)
