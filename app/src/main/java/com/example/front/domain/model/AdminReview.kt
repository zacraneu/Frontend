package com.example.front.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class AdminApplicationsPage(
    val content: List<ApplicationDetail>,
    val totalElements: Int,
    val totalPages: Int,
    val currentPage: Int
)

@Serializable
data class AdminReviewRequest(
    val rejectionReason: String
)

@Serializable
data class AdminActionResponse(
    val id: String,
    val status: ApplicationStatus,
    val reviewedAt: String? = null,
    val rejectionReason: String? = null
)

enum class AdminStatusFilter(val apiValue: String?) {
    ALL(null),
    NEW(ApplicationStatus.NEW.name),
    REVIEWING(ApplicationStatus.REVIEWING.name),
    APPROVED(ApplicationStatus.APPROVED.name),
    REJECTED(ApplicationStatus.REJECTED.name),
    RETURNED(ApplicationStatus.RETURNED.name)
}
