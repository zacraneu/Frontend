package com.example.front.domain.usecase

import com.example.front.domain.model.ApplicationDetail
import com.example.front.domain.repository.ApplicationRepository
import javax.inject.Inject

class GetApplicationDetailUseCase @Inject constructor(
    private val applicationRepository: ApplicationRepository
) {
    suspend operator fun invoke(applicationId: String): ApplicationDetail =
        applicationRepository.getApplication(applicationId)
}
