package com.example.front.ui.screens

import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.front.R
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.front.domain.model.ApplicationDetail
import com.example.front.ui.components.ApplicationStatusBadge
import com.example.front.ui.components.DocumentItem
import com.example.front.ui.components.LoadingIndicator
import com.example.front.utils.DateFormatter
import com.example.front.viewmodel.ApplicationDetailUiState
import com.example.front.viewmodel.ApplicationDetailViewModel
import com.example.front.viewmodel.DocumentDownloadUiState

@Composable
fun ApplicationDetailScreen(
    onBack: () -> Unit,
    onEditApplication: (String) -> Unit,
    viewModel: ApplicationDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val downloadState by viewModel.downloadState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.loadApplication()
    }

    LaunchedEffect(downloadState) {
        when (val state = downloadState) {
            is DocumentDownloadUiState.Success -> {
                val uri = FileProvider.getUriForFile(
                    context,
                    "${context.packageName}.fileprovider",
                    state.file
                )
                val intent = Intent(Intent.ACTION_VIEW).apply {
                    setDataAndType(uri, context.contentResolver.getType(uri))
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }
                runCatching { context.startActivity(intent) }
                    .onFailure {
                        Toast.makeText(
                            context,
                            context.getString(R.string.file_saved, state.file.name),
                            Toast.LENGTH_LONG
                        ).show()
                    }
                viewModel.resetDownloadState()
            }
            is DocumentDownloadUiState.Error -> {
                snackbarHostState.showSnackbar(state.message)
                Toast.makeText(context, state.message, Toast.LENGTH_SHORT).show()
                viewModel.resetDownloadState()
            }
            else -> Unit
        }
    }

    Scaffold(snackbarHost = { SnackbarHost(snackbarHostState) }) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(stringResource(R.string.application_details), style = MaterialTheme.typography.headlineMedium)

            OutlinedButton(onClick = viewModel::loadApplication, modifier = Modifier.fillMaxWidth()) {
                Text(stringResource(R.string.refresh))
            }

            when (val state = uiState) {
                ApplicationDetailUiState.Loading -> LoadingIndicator()
                is ApplicationDetailUiState.Error -> {
                    Text(state.message, color = MaterialTheme.colorScheme.error)
                    Button(onClick = viewModel::loadApplication) {
                        Text(stringResource(R.string.retry))
                    }
                }
                is ApplicationDetailUiState.Success -> DetailContent(
                    application = state.application,
                    onDownloadDocument = viewModel::downloadDocument,
                    onEdit = { onEditApplication(state.application.id) },
                    canEdit = viewModel.canEdit(state.application.status)
                )
            }

            OutlinedButton(onClick = onBack, modifier = Modifier.fillMaxWidth()) {
                Text(stringResource(R.string.back))
            }
        }
    }
}

@Composable
private fun DetailContent(
    application: ApplicationDetail,
    onDownloadDocument: (String, String) -> Unit,
    onEdit: () -> Unit,
    canEdit: Boolean
) {
    RowStatus(application)

    DetailField(stringResource(R.string.field_id), DateFormatter.shortId(application.id))
    DetailField(stringResource(R.string.label_submitted), DateFormatter.formatIso(application.createdAt))
    DetailField(stringResource(R.string.label_updated), DateFormatter.formatIso(application.updatedAt))
    application.reviewedAt?.let {
        DetailField(stringResource(R.string.label_reviewed), DateFormatter.formatIso(it))
    }
    DetailField(stringResource(R.string.full_name), application.fullName)
    DetailField(stringResource(R.string.email), application.email)
    DetailField(stringResource(R.string.phone), application.phone)
    DetailField(stringResource(R.string.label_reason), application.submissionReason)
    application.additionalInfo?.let {
        DetailField(stringResource(R.string.label_additional_short), it)
    }
    application.rejectionReason?.let {
        DetailField(stringResource(R.string.admin_comment), it)
    }

    Text(stringResource(R.string.documents_section), style = MaterialTheme.typography.titleMedium)
    if (application.documents.isEmpty()) {
        Text(stringResource(R.string.no_documents), style = MaterialTheme.typography.bodyMedium)
    } else {
        application.documents.forEach { document ->
            DocumentItem(
                name = document.originalFilename ?: document.filename,
                sizeBytes = document.fileSize,
                onDownload = {
                    onDownloadDocument(document.id, document.filename)
                }
            )
        }
    }

    if (canEdit) {
        Button(onClick = onEdit, modifier = Modifier.fillMaxWidth()) {
            Text(stringResource(R.string.upload_corrected_documents))
        }
    }
}

@Composable
private fun RowStatus(application: ApplicationDetail) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        ApplicationStatusBadge(status = application.status)
    }
}

@Composable
private fun DetailField(label: String, value: String) {
    Column {
        Text(label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.outline)
        Text(value, style = MaterialTheme.typography.bodyLarge)
    }
}
