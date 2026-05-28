package com.example.front.data.repository

import com.example.front.data.di.Authenticated
import com.example.front.data.remote.ApiService
import com.example.front.domain.model.AdminActionResponse
import com.example.front.domain.model.AdminApplicationsPage
import com.example.front.domain.model.AdminReviewRequest
import com.example.front.domain.model.ApplicationDetail
import com.example.front.domain.repository.AdminRepository
import javax.inject.Inject

class AdminRepositoryImpl @Inject constructor(
    @Authenticated private val apiService: ApiService
) : AdminRepository {
    override suspend fun getApplications(status: String?, page: Int, size: Int): AdminApplicationsPage =
        apiService.getAdminApplications(status = status, page = page, size = size)

    override suspend fun getApplication(applicationId: String): ApplicationDetail =
        apiService.getAdminApplication(applicationId)

    override suspend fun downloadDocument(applicationId: String, documentId: String): ByteArray =
        apiService.downloadAdminDocument(applicationId, documentId).bytes()

    override suspend fun approve(applicationId: String): AdminActionResponse =
        apiService.approveApplication(applicationId)

    override suspend fun reject(applicationId: String, reason: String): AdminActionResponse =
        apiService.rejectApplication(applicationId, AdminReviewRequest(reason))

    override suspend fun returnForRevision(applicationId: String, comment: String): AdminActionResponse =
        apiService.returnApplication(applicationId, AdminReviewRequest(comment))
}
