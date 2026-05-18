package com.example.front.domain.usecase

import com.example.front.domain.model.ApplicationsPage
import com.example.front.domain.repository.ApplicationRepository
import javax.inject.Inject

class GetApplicationsUseCase @Inject constructor(
    private val applicationRepository: ApplicationRepository
) {
    suspend operator fun invoke(page: Int = 0, size: Int = 20): ApplicationsPage =
        applicationRepository.getApplications(page, size)
}
