package com.example.front.domain.usecase

import com.example.front.domain.model.AdminActionResponse
import com.example.front.domain.repository.AdminRepository
import javax.inject.Inject

class ApproveApplicationUseCase @Inject constructor(
    private val adminRepository: AdminRepository
) {
    suspend operator fun invoke(applicationId: String): AdminActionResponse =
        adminRepository.approve(applicationId)
}
