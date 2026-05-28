package com.example.front.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.front.domain.model.AdminStatusFilter
import com.example.front.domain.model.ApplicationDetail
import com.example.front.domain.usecase.GetAdminApplicationsUseCase
import com.example.front.utils.ApiErrorMapper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AdminApplicationListViewModel @Inject constructor(
    private val getAdminApplicationsUseCase: GetAdminApplicationsUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow<AdminApplicationListUiState>(AdminApplicationListUiState.Loading)
    val uiState: StateFlow<AdminApplicationListUiState> = _uiState.asStateFlow()

    private val _selectedFilter = MutableStateFlow(AdminStatusFilter.ALL)
    val selectedFilter: StateFlow<AdminStatusFilter> = _selectedFilter.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    init {
        loadApplications(showLoading = true)
    }

    fun selectFilter(filter: AdminStatusFilter) {
        if (_selectedFilter.value == filter) return
        _selectedFilter.value = filter
        loadApplications(showLoading = true)
    }

    fun refresh() {
        loadApplications(showLoading = false, pullToRefresh = true)
    }

    fun loadApplications(showLoading: Boolean = true, pullToRefresh: Boolean = false) {
        viewModelScope.launch {
            if (pullToRefresh) {
                _isRefreshing.value = true
            } else if (showLoading) {
                _uiState.value = AdminApplicationListUiState.Loading
            }

            _uiState.value = runCatching {
                getAdminApplicationsUseCase(status = _selectedFilter.value.apiValue)
            }.fold(
                onSuccess = { AdminApplicationListUiState.Success(it.content) },
                onFailure = { AdminApplicationListUiState.Error(ApiErrorMapper.message(it)) }
            )

            _isRefreshing.value = false
        }
    }
}

sealed interface AdminApplicationListUiState {
    data object Loading : AdminApplicationListUiState
    data class Success(val applications: List<ApplicationDetail>) : AdminApplicationListUiState
    data class Error(val message: String) : AdminApplicationListUiState
}
