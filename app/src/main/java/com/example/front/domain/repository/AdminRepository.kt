package com.example.front.domain.repository

import com.example.front.domain.model.AdminActionResponse
import com.example.front.domain.model.AdminApplicationsPage
import com.example.front.domain.model.ApplicationDetail

interface AdminRepository {
    suspend fun getApplications(status: String?, page: Int = 0, size: Int = 50): AdminApplicationsPage
    suspend fun getApplication(applicationId: String): ApplicationDetail
    suspend fun downloadDocument(applicationId: String, documentId: String): ByteArray
    suspend fun approve(applicationId: String): AdminActionResponse
    suspend fun reject(applicationId: String, reason: String): AdminActionResponse
    suspend fun returnForRevision(applicationId: String, comment: String): AdminActionResponse
}
