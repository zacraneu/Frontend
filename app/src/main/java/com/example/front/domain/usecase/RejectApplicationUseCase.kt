package com.example.front.domain.usecase

import com.example.front.domain.model.AdminActionResponse
import com.example.front.domain.repository.AdminRepository
import javax.inject.Inject

class RejectApplicationUseCase @Inject constructor(
    private val adminRepository: AdminRepository
) {
    suspend operator fun invoke(applicationId: String, reason: String): AdminActionResponse {
        require(reason.trim().length >= 10) { "Причина отклонения должна содержать минимум 10 символов" }
        require(reason.length <= 500) { "Причина отклонения не должна превышать 500 символов" }
        return adminRepository.reject(applicationId, reason.trim())
    }
}
