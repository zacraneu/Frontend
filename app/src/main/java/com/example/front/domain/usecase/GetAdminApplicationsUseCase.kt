package com.example.front.domain.usecase

import com.example.front.domain.model.AdminApplicationsPage
import com.example.front.domain.repository.AdminRepository
import javax.inject.Inject

class GetAdminApplicationsUseCase @Inject constructor(
    private val adminRepository: AdminRepository
) {
    suspend operator fun invoke(status: String?, page: Int = 0, size: Int = 50): AdminApplicationsPage =
        adminRepository.getApplications(status = status, page = page, size = size)
}
