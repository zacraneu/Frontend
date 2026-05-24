package com.example.front.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.front.domain.model.ApplicationSummary
import com.example.front.domain.usecase.GetApplicationsUseCase
import com.example.front.utils.ApiErrorMapper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ApplicationListViewModel @Inject constructor(
    private val getApplicationsUseCase: GetApplicationsUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow<ApplicationListUiState>(ApplicationListUiState.Loading)
    val uiState: StateFlow<ApplicationListUiState> = _uiState.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    private var cachedApplications: List<ApplicationSummary> = emptyList()

    init {
        loadApplications(showFullScreenLoading = true)
    }

    fun refresh() {
        loadApplications(showFullScreenLoading = false, isPullToRefresh = true)
    }

    private fun loadApplications(
        showFullScreenLoading: Boolean,
        isPullToRefresh: Boolean = false
    ) {
        viewModelScope.launch {
            if (isPullToRefresh) {
                _isRefreshing.value = true
            } else if (showFullScreenLoading) {
                _uiState.value = ApplicationListUiState.Loading
            }

            _uiState.value = runCatching {
                getApplicationsUseCase()
            }.fold(
                onSuccess = { page ->
                    cachedApplications = page.content
                    ApplicationListUiState.Success(page.content)
                },
                onFailure = { error ->
                    if (cachedApplications.isNotEmpty()) {
                        ApplicationListUiState.Offline(
                            applications = cachedApplications,
                            message = ApiErrorMapper.message(error)
                        )
                    } else {
                        ApplicationListUiState.Error(ApiErrorMapper.message(error))
                    }
                }
            )

            _isRefreshing.value = false
        }
    }
}

sealed interface ApplicationListUiState {
    data object Loading : ApplicationListUiState
    data class Success(val applications: List<ApplicationSummary>) : ApplicationListUiState
    data class Offline(val applications: List<ApplicationSummary>, val message: String) : ApplicationListUiState
    data class Error(val message: String) : ApplicationListUiState
}
