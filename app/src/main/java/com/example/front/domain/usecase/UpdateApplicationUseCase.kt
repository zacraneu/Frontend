package com.example.front.domain.usecase

import com.example.front.domain.model.ApplicationMutationResponse
import com.example.front.domain.repository.ApplicationRepository
import com.example.front.utils.Constants
import com.example.front.utils.ValidationUtils
import java.io.File
import javax.inject.Inject

class UpdateApplicationUseCase @Inject constructor(
    private val applicationRepository: ApplicationRepository
) {
    suspend operator fun invoke(
        applicationId: String,
        fullName: String,
        email: String,
        phone: String,
        submissionReason: String,
        additionalInfo: String?,
        documents: List<File>
    ): ApplicationMutationResponse {
        require(ValidationUtils.isRequiredTextValid(fullName)) { "ФИО не может быть пустым" }
        require(ValidationUtils.isEmailValid(email)) { "Некорректный email" }
        require(ValidationUtils.isPhoneValid(phone)) { "Телефон должен быть в формате +7-XXX-XXX-XX-XX" }
        require(ValidationUtils.isRequiredTextValid(submissionReason)) { "Причина подачи не может быть пустой" }
        require(documents.isNotEmpty()) { "Загрузите минимум 1 документ" }
        require(documents.size <= Constants.MAX_DOCUMENTS) { "Максимум 5 документов" }
        require(documents.all { it.length() <= Constants.MAX_FILE_SIZE_BYTES }) { "Каждый файл должен быть меньше 10 MB" }
        require(documents.sumOf { it.length() } <= Constants.MAX_TOTAL_FILE_SIZE_BYTES) {
            "Общий размер документов должен быть меньше 30 MB"
        }

        return applicationRepository.updateApplication(
            applicationId = applicationId,
            fullName = fullName.trim(),
            email = email.trim(),
            phone = phone.trim(),
            submissionReason = submissionReason.trim(),
            additionalInfo = additionalInfo?.trim(),
            documents = documents
        )
    }
}
