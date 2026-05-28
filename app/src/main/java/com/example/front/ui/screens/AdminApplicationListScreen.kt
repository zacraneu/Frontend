package com.example.front.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.front.R
import com.example.front.domain.model.AdminStatusFilter
import com.example.front.ui.components.AdminApplicationListItem
import com.example.front.ui.components.LoadingIndicator
import com.example.front.viewmodel.AdminApplicationListUiState
import com.example.front.viewmodel.AdminApplicationListViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminApplicationListScreen(
    onOpenApplication: (String) -> Unit,
    onOpenProfile: () -> Unit,
    viewModel: AdminApplicationListViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val selectedFilter by viewModel.selectedFilter.collectAsState()
    val isRefreshing by viewModel.isRefreshing.collectAsState()

    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.admin_applications_title),
                    style = MaterialTheme.typography.headlineMedium
                )
                OutlinedButton(onClick = onOpenProfile) {
                    Text(stringResource(R.string.profile))
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                AdminStatusFilter.entries.forEach { filter ->
                    AssistChip(
                        onClick = { viewModel.selectFilter(filter) },
                        label = { Text(filter.toLabel()) },
                        enabled = selectedFilter != filter
                    )
                }
            }

            PullToRefreshBox(
                isRefreshing = isRefreshing,
                onRefresh = viewModel::refresh,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 12.dp)
            ) {
                when (val state = uiState) {
                    AdminApplicationListUiState.Loading -> LoadingIndicator()
                    is AdminApplicationListUiState.Error -> ErrorContent(
                        message = state.message,
                        onRetry = viewModel::loadApplications
                    )
                    is AdminApplicationListUiState.Success -> {
                        if (state.applications.isEmpty()) {
                            EmptyState()
                        } else {
                            LazyColumn(
                                contentPadding = PaddingValues(horizontal = 24.dp, vertical = 8.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                items(state.applications, key = { it.id }) { app ->
                                    AdminApplicationListItem(
                                        application = app,
                                        onClick = { onOpenApplication(app.id) }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ErrorContent(message: String, onRetry: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = message, color = MaterialTheme.colorScheme.error)
        Button(onClick = onRetry, modifier = Modifier.padding(top = 16.dp)) {
            Text(stringResource(R.string.retry))
        }
    }
}

@Composable
private fun EmptyState() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(stringResource(R.string.admin_empty_applications))
    }
}

@Composable
private fun AdminStatusFilter.toLabel(): String = stringResource(
    when (this) {
        AdminStatusFilter.ALL -> R.string.filter_all
        AdminStatusFilter.NEW -> R.string.filter_new
        AdminStatusFilter.REVIEWING -> R.string.filter_reviewing
        AdminStatusFilter.APPROVED -> R.string.filter_approved
        AdminStatusFilter.REJECTED -> R.string.filter_rejected
        AdminStatusFilter.RETURNED -> R.string.filter_returned
    }
)
