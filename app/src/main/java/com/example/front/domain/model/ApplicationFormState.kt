package com.example.front.domain.model

import com.example.front.utils.Constants
import com.example.front.utils.PhoneFormatter
import com.example.front.utils.ValidationUtils

data class ApplicationFormState(
    val fullName: String = "",
    val email: String = "",
    val phone: String = "",
    val submissionReason: String = "",
    val additionalInfo: String = "",
    val documents: List<DocumentUpload> = emptyList(),
    val isProfileLoading: Boolean = false
) {
    fun validate(): String? {
        if (!ValidationUtils.isRequiredTextValid(fullName)) return "ФИО не может быть пустым"
        if (!ValidationUtils.isEmailValid(email)) return "Некорректный email"
        if (!ValidationUtils.isPhoneValid(PhoneFormatter.formatDigits(phone))) {
            return "Телефон должен быть в формате +7-XXX-XXX-XX-XX"
        }
        if (!ValidationUtils.isRequiredTextValid(submissionReason)) {
            return "Причина подачи не может быть пустой"
        }
        if (submissionReason.length > Constants.MAX_SUBMISSION_REASON_LENGTH) {
            return "Причина подачи не должна превышать ${Constants.MAX_SUBMISSION_REASON_LENGTH} символов"
        }
        if (documents.isEmpty()) return "Загрузите минимум 1 документ"
        if (documents.size > Constants.MAX_DOCUMENTS) return "Максимум 5 документов"
        if (documents.any { it.file.length() > Constants.MAX_FILE_SIZE_BYTES }) {
            return "Каждый файл должен быть меньше 10 MB"
        }
        if (documents.sumOf { it.file.length() } > Constants.MAX_TOTAL_FILE_SIZE_BYTES) {
            return "Общий размер документов должен быть меньше 30 MB"
        }
        return null
    }
}
