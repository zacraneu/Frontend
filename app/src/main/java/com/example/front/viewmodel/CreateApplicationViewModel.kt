package com.example.front.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.front.data.local.TokenStorage
import com.example.front.domain.model.ApplicationFormState
import com.example.front.domain.model.DocumentUpload
import com.example.front.domain.repository.UserRepository
import com.example.front.domain.usecase.CreateApplicationUseCase
import com.example.front.utils.ApiErrorMapper
import com.example.front.utils.Constants
import com.example.front.utils.DocumentFileResolver
import com.example.front.utils.FileUtils
import com.example.front.utils.ImageCompression
import com.example.front.utils.PhoneFormatter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateApplicationViewModel @Inject constructor(
    private val createApplicationUseCase: CreateApplicationUseCase,
    private val userRepository: UserRepository,
    private val tokenStorage: TokenStorage,
    private val documentFileResolver: DocumentFileResolver,
    private val imageCompression: ImageCompression
) : ViewModel() {
    private val _formState = MutableStateFlow(ApplicationFormState())
    val formState: StateFlow<ApplicationFormState> = _formState.asStateFlow()

    private val _submitState = MutableStateFlow<SubmitState>(SubmitState.Idle)
    val submitState: StateFlow<SubmitState> = _submitState.asStateFlow()

    init {
        loadProfilePrefill()
    }

    fun updateFullName(value: String) = updateForm { it.copy(fullName = value) }

    fun updateEmail(value: String) = updateForm { it.copy(email = value) }

    fun updatePhone(value: String) =
        updateForm { it.copy(phone = PhoneFormatter.normalizeDigits(value)) }

    fun updateSubmissionReason(value: String) {
        val trimmed = value.take(Constants.MAX_SUBMISSION_REASON_LENGTH)
        updateForm { it.copy(submissionReason = trimmed) }
    }

    fun updateAdditionalInfo(value: String) = updateForm { it.copy(additionalInfo = value) }

    fun clearSubmitError() {
        if (_submitState.value is SubmitState.Error) {
            _submitState.value = SubmitState.Idle
        }
    }

    fun addDocument(uri: Uri) {
        viewModelScope.launch {
            try {
                val resolved = documentFileResolver.resolve(uri)
                val file = resolved.file

                if (!FileUtils.isAllowedDocument(file)) {
                    emitSubmitError("Допустимы только PDF, JPEG и PNG")
                    return@launch
                }
                if (!FileUtils.isFileSizeAllowed(file)) {
                    emitSubmitError("Файл больше 10 MB")
                    return@launch
                }

                val compressed = if (shouldCompress(resolved.displayName)) {
                    imageCompression.compress(file)
                } else {
                    file
                }

                if (!FileUtils.isFileSizeAllowed(compressed)) {
                    emitSubmitError("Файл после сжатия больше 10 MB")
                    return@launch
                }

                val currentDocuments = _formState.value.documents
                if (currentDocuments.size >= Constants.MAX_DOCUMENTS) {
                    emitSubmitError("Максимум 5 документов")
                    return@launch
                }
                val totalSize = currentDocuments.sumOf { it.file.length() } + compressed.length()
                if (totalSize > Constants.MAX_TOTAL_FILE_SIZE_BYTES) {
                    emitSubmitError("Общий размер документов должен быть меньше 30 MB")
                    return@launch
                }

                val document = DocumentUpload(
                    uri = uri,
                    file = compressed,
                    name = resolved.displayName
                )
                _formState.update { it.copy(documents = it.documents + document) }
            } catch (e: Exception) {
                emitSubmitError(e.message ?: "Не удалось добавить документ")
            }
        }
    }

    fun removeDocument(index: Int) {
        _formState.update { state ->
            if (index !in state.documents.indices) return@update state
            val updated = state.documents.toMutableList().apply { removeAt(index) }
            state.copy(documents = updated)
        }
    }

    fun submitApplication() {
        viewModelScope.launch {
            val form = _formState.value
            val validationError = form.validate()
            if (validationError != null) {
                _submitState.value = SubmitState.Error(validationError)
                return@launch
            }

            _submitState.value = SubmitState.Loading
            _submitState.value = runCatching {
                createApplicationUseCase(
                    fullName = form.fullName,
                    email = form.email,
                    phone = PhoneFormatter.formatDigits(form.phone),
                    submissionReason = form.submissionReason,
                    additionalInfo = form.additionalInfo.takeIf { it.isNotBlank() },
                    documents = form.documents.map { it.file }
                )
            }.fold(
                onSuccess = { SubmitState.Success(it.id) },
                onFailure = { SubmitState.Error(ApiErrorMapper.message(it)) }
            )
        }
    }

    fun resetSubmitState() {
        _submitState.value = SubmitState.Idle
    }

    private fun loadProfilePrefill() {
        viewModelScope.launch {
            _formState.update { it.copy(isProfileLoading = true) }
            val prefill = runCatching { userRepository.getProfile() }
                .map { user ->
                    Triple(user.fullName, user.email, user.phone.orEmpty())
                }
                .getOrElse {
                    Triple(
                        tokenStorage.getFullName().orEmpty(),
                        tokenStorage.getEmail().orEmpty(),
                        ""
                    )
                }

            _formState.update {
                it.copy(
                    fullName = prefill.first,
                    email = prefill.second,
                    phone = PhoneFormatter.parseToDigits(prefill.third).ifBlank { it.phone },
                    isProfileLoading = false
                )
            }
        }
    }

    private fun updateForm(transform: (ApplicationFormState) -> ApplicationFormState) {
        _formState.update(transform)
        clearSubmitError()
    }

    private fun emitSubmitError(message: String) {
        _submitState.value = SubmitState.Error(message)
    }

    private fun shouldCompress(fileName: String): Boolean {
        val lower = fileName.lowercase()
        return lower.endsWith(".jpg") ||
            lower.endsWith(".jpeg") ||
            lower.endsWith(".png")
    }
}

sealed interface SubmitState {
    data object Idle : SubmitState
    data object Loading : SubmitState
    data class Success(val applicationId: String) : SubmitState
    data class Error(val message: String) : SubmitState
}
