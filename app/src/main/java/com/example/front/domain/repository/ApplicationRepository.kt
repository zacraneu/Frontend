package com.example.front.domain.repository

import com.example.front.domain.model.ApplicationDetail
import com.example.front.domain.model.ApplicationMutationResponse
import com.example.front.domain.model.ApplicationsPage
import java.io.File

interface ApplicationRepository {
    suspend fun getApplications(page: Int = 0, size: Int = 20): ApplicationsPage
    suspend fun getApplication(applicationId: String): ApplicationDetail
    suspend fun createApplication(
        fullName: String,
        email: String,
        phone: String,
        submissionReason: String,
        additionalInfo: String?,
        documents: List<File>
    ): ApplicationMutationResponse

    suspend fun updateApplication(
        applicationId: String,
        fullName: String,
        email: String,
        phone: String,
        submissionReason: String,
        additionalInfo: String?,
        documents: List<File>
    ): ApplicationMutationResponse
}
