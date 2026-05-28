package com.example.front.domain.usecase

import com.example.front.domain.model.AdminActionResponse
import com.example.front.domain.repository.AdminRepository
import javax.inject.Inject

class ReturnApplicationUseCase @Inject constructor(
    private val adminRepository: AdminRepository
) {
    suspend operator fun invoke(applicationId: String, comment: String?): AdminActionResponse {
        val normalized = comment.orEmpty().trim()
        require(normalized.length <= 500) { "Комментарий не должен превышать 500 символов" }
        return adminRepository.returnForRevision(applicationId, normalized)
    }
}
