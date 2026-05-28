package com.example.front.domain.usecase

import com.example.front.domain.model.ApplicationDetail
import com.example.front.domain.repository.AdminRepository
import javax.inject.Inject

class GetAdminApplicationDetailUseCase @Inject constructor(
    private val adminRepository: AdminRepository
) {
    suspend operator fun invoke(applicationId: String): ApplicationDetail =
        adminRepository.getApplication(applicationId)
}
