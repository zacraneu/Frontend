package com.example.front.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.front.R
import com.example.front.ui.components.ApplicationListItem
import com.example.front.ui.components.LoadingIndicator
import com.example.front.viewmodel.ApplicationListUiState
import com.example.front.viewmodel.ApplicationListViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ApplicationListScreen(
    onCreateApplication: () -> Unit,
    onOpenApplication: (String) -> Unit,
    onOpenProfile: () -> Unit,
    viewModel: ApplicationListViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val isRefreshing by viewModel.isRefreshing.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.refresh()
    }

    val newApplicationLabel = stringResource(R.string.new_application)

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = onCreateApplication,
                modifier = Modifier.semantics { contentDescription = newApplicationLabel }
            ) {
                Text("+")
            }
        }
    ) { padding ->
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
                    stringResource(R.string.applications_title),
                    style = MaterialTheme.typography.headlineMedium
                )
                OutlinedButton(onClick = onOpenProfile) {
                    Text(stringResource(R.string.profile))
                }
            }

            PullToRefreshBox(
                isRefreshing = isRefreshing,
                onRefresh = viewModel::refresh,
                modifier = Modifier.fillMaxSize()
            ) {
                when (val state = uiState) {
                    ApplicationListUiState.Loading -> LoadingIndicator()
                    is ApplicationListUiState.Error -> ErrorContent(
                        message = state.message,
                        onRetry = viewModel::refresh
                    )
                    is ApplicationListUiState.Offline -> ApplicationsContent(
                        applications = state.applications,
                        bannerMessage = state.message,
                        onOpenApplication = onOpenApplication
                    )
                    is ApplicationListUiState.Success -> ApplicationsContent(
                        applications = state.applications,
                        bannerMessage = null,
                        onOpenApplication = onOpenApplication
                    )
                }
            }
        }
    }
}

@Composable
private fun ApplicationsContent(
    applications: List<com.example.front.domain.model.ApplicationSummary>,
    bannerMessage: String?,
    onOpenApplication: (String) -> Unit
) {
    if (applications.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = stringResource(R.string.empty_applications),
                style = MaterialTheme.typography.bodyLarge
            )
        }
        return
    }

    LazyColumn(
        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        bannerMessage?.let { message ->
            item {
                Text(
                    text = message,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }
        }
        items(applications, key = { it.id }) { application ->
            ApplicationListItem(
                application = application,
                onClick = { onOpenApplication(application.id) }
            )
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
