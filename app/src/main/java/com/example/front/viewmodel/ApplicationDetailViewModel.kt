package com.example.front.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.front.domain.model.ApplicationDetail
import com.example.front.domain.model.ApplicationStatus
import com.example.front.domain.repository.ApplicationRepository
import com.example.front.domain.usecase.GetApplicationDetailUseCase
import com.example.front.utils.ApiErrorMapper
import com.example.front.utils.DocumentDownloadHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class ApplicationDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getApplicationDetailUseCase: GetApplicationDetailUseCase,
    private val applicationRepository: ApplicationRepository,
    private val documentDownloadHelper: DocumentDownloadHelper
) : ViewModel() {
    private val applicationId: String = checkNotNull(savedStateHandle["applicationId"])

    private val _uiState = MutableStateFlow<ApplicationDetailUiState>(ApplicationDetailUiState.Loading)
    val uiState: StateFlow<ApplicationDetailUiState> = _uiState.asStateFlow()

    private val _downloadState = MutableStateFlow<DocumentDownloadUiState>(DocumentDownloadUiState.Idle)
    val downloadState: StateFlow<DocumentDownloadUiState> = _downloadState.asStateFlow()

    init {
        loadApplication()
    }

    fun loadApplication() {
        viewModelScope.launch {
            _uiState.value = ApplicationDetailUiState.Loading
            _uiState.value = runCatching {
                getApplicationDetailUseCase(applicationId)
            }.fold(
                onSuccess = ApplicationDetailUiState::Success,
                onFailure = { ApplicationDetailUiState.Error(ApiErrorMapper.message(it)) }
            )
        }
    }

    fun downloadDocument(documentId: String, fileName: String) {
        viewModelScope.launch {
            _downloadState.value = DocumentDownloadUiState.Loading
            _downloadState.value = runCatching {
                val bytes = applicationRepository.downloadDocument(applicationId, documentId)
                val file = documentDownloadHelper.save(applicationId, fileName, bytes)
                DocumentDownloadUiState.Success(file)
            }.getOrElse {
                DocumentDownloadUiState.Error(ApiErrorMapper.message(it))
            }
        }
    }

    fun resetDownloadState() {
        _downloadState.value = DocumentDownloadUiState.Idle
    }

    fun canEdit(status: ApplicationStatus): Boolean = status == ApplicationStatus.RETURNED
}

sealed interface ApplicationDetailUiState {
    data object Loading : ApplicationDetailUiState
    data class Success(val application: ApplicationDetail) : ApplicationDetailUiState
    data class Error(val message: String) : ApplicationDetailUiState
}

sealed interface DocumentDownloadUiState {
    data object Idle : DocumentDownloadUiState
    data object Loading : DocumentDownloadUiState
    data class Success(val file: File) : DocumentDownloadUiState
    data class Error(val message: String) : DocumentDownloadUiState
}
