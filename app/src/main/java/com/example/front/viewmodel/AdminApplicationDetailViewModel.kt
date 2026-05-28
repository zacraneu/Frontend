package com.example.front.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.front.domain.model.ApplicationDetail
import com.example.front.domain.model.ApplicationStatus
import com.example.front.domain.repository.AdminRepository
import com.example.front.domain.usecase.ApproveApplicationUseCase
import com.example.front.domain.usecase.GetAdminApplicationDetailUseCase
import com.example.front.domain.usecase.RejectApplicationUseCase
import com.example.front.domain.usecase.ReturnApplicationUseCase
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
class AdminApplicationDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getAdminApplicationDetailUseCase: GetAdminApplicationDetailUseCase,
    private val approveApplicationUseCase: ApproveApplicationUseCase,
    private val rejectApplicationUseCase: RejectApplicationUseCase,
    private val returnApplicationUseCase: ReturnApplicationUseCase,
    private val adminRepository: AdminRepository,
    private val documentDownloadHelper: DocumentDownloadHelper
) : ViewModel() {
    private val applicationId: String = checkNotNull(savedStateHandle["applicationId"])

    private val _uiState = MutableStateFlow<AdminApplicationDetailUiState>(AdminApplicationDetailUiState.Loading)
    val uiState: StateFlow<AdminApplicationDetailUiState> = _uiState.asStateFlow()

    private val _actionState = MutableStateFlow<AdminActionState>(AdminActionState.Idle)
    val actionState: StateFlow<AdminActionState> = _actionState.asStateFlow()

    private val _downloadState = MutableStateFlow<DocumentDownloadUiState>(DocumentDownloadUiState.Idle)
    val downloadState: StateFlow<DocumentDownloadUiState> = _downloadState.asStateFlow()

    init {
        loadApplication()
    }

    fun loadApplication() {
        viewModelScope.launch {
            _uiState.value = AdminApplicationDetailUiState.Loading
            _uiState.value = runCatching {
                getAdminApplicationDetailUseCase(applicationId)
            }.fold(
                onSuccess = AdminApplicationDetailUiState::Success,
                onFailure = { AdminApplicationDetailUiState.Error(ApiErrorMapper.message(it)) }
            )
        }
    }

    fun approve() {
        viewModelScope.launch {
            _actionState.value = AdminActionState.Loading
            _actionState.value = runCatching {
                approveApplicationUseCase(applicationId)
                loadApplication()
                AdminActionState.Success("Заявка одобрена")
            }.getOrElse { AdminActionState.Error(ApiErrorMapper.message(it)) }
        }
    }

    fun reject(reason: String) {
        viewModelScope.launch {
            _actionState.value = AdminActionState.Loading
            _actionState.value = runCatching {
                rejectApplicationUseCase(applicationId, reason)
                loadApplication()
                AdminActionState.Success("Заявка отклонена")
            }.getOrElse { AdminActionState.Error(ApiErrorMapper.message(it)) }
        }
    }

    fun returnForRevision(comment: String?) {
        viewModelScope.launch {
            _actionState.value = AdminActionState.Loading
            _actionState.value = runCatching {
                returnApplicationUseCase(applicationId, comment)
                loadApplication()
                AdminActionState.Success("Заявка возвращена на доработку")
            }.getOrElse { AdminActionState.Error(ApiErrorMapper.message(it)) }
        }
    }

    fun downloadDocument(documentId: String, fileName: String) {
        viewModelScope.launch {
            _downloadState.value = DocumentDownloadUiState.Loading
            _downloadState.value = runCatching {
                val bytes = adminRepository.downloadDocument(applicationId, documentId)
                val file = documentDownloadHelper.save(applicationId, fileName, bytes)
                DocumentDownloadUiState.Success(file)
            }.getOrElse {
                DocumentDownloadUiState.Error(ApiErrorMapper.message(it))
            }
        }
    }

    fun canReview(status: ApplicationStatus): Boolean =
        status != ApplicationStatus.APPROVED && status != ApplicationStatus.REJECTED

    fun resetActionState() {
        _actionState.value = AdminActionState.Idle
    }

    fun resetDownloadState() {
        _downloadState.value = DocumentDownloadUiState.Idle
    }
}

sealed interface AdminApplicationDetailUiState {
    data object Loading : AdminApplicationDetailUiState
    data class Success(val application: ApplicationDetail) : AdminApplicationDetailUiState
    data class Error(val message: String) : AdminApplicationDetailUiState
}

sealed interface AdminActionState {
    data object Idle : AdminActionState
    data object Loading : AdminActionState
    data class Success(val message: String) : AdminActionState
    data class Error(val message: String) : AdminActionState
}
