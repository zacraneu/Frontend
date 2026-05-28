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
import androidx.compose.material3.AlertDialog
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.front.R
import com.example.front.domain.model.ApplicationDetail
import com.example.front.ui.components.ApplicationStatusBadge
import com.example.front.ui.components.DocumentItem
import com.example.front.ui.components.LoadingIndicator
import com.example.front.ui.components.RejectApplicationBottomSheet
import com.example.front.ui.components.ReturnApplicationBottomSheet
import com.example.front.utils.DateFormatter
import com.example.front.viewmodel.AdminActionState
import com.example.front.viewmodel.AdminApplicationDetailUiState
import com.example.front.viewmodel.AdminApplicationDetailViewModel
import com.example.front.viewmodel.DocumentDownloadUiState

@Composable
fun AdminApplicationDetailScreen(
    onBack: () -> Unit,
    viewModel: AdminApplicationDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val actionState by viewModel.actionState.collectAsState()
    val downloadState by viewModel.downloadState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    var showApproveConfirm by remember { mutableStateOf(false) }
    var showRejectSheet by remember { mutableStateOf(false) }
    var showReturnSheet by remember { mutableStateOf(false) }

    LaunchedEffect(actionState) {
        when (val state = actionState) {
            is AdminActionState.Success -> {
                Toast.makeText(context, state.message, Toast.LENGTH_SHORT).show()
                viewModel.resetActionState()
            }
            is AdminActionState.Error -> {
                snackbarHostState.showSnackbar(state.message)
                viewModel.resetActionState()
            }
            else -> Unit
        }
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
            Text(stringResource(R.string.admin_application_details), style = MaterialTheme.typography.headlineMedium)

            when (val state = uiState) {
                AdminApplicationDetailUiState.Loading -> LoadingIndicator()
                is AdminApplicationDetailUiState.Error -> {
                    Text(state.message, color = MaterialTheme.colorScheme.error)
                    Button(onClick = viewModel::loadApplication) {
                        Text(stringResource(R.string.retry))
                    }
                }
                is AdminApplicationDetailUiState.Success -> {
                    DetailContent(state.application, viewModel::downloadDocument)
                    if (viewModel.canReview(state.application.status)) {
                        Button(
                            onClick = { showApproveConfirm = true },
                            enabled = actionState !is AdminActionState.Loading,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(stringResource(R.string.approve_action))
                        }
                        Button(
                            onClick = { showReturnSheet = true },
                            enabled = actionState !is AdminActionState.Loading,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(stringResource(R.string.return_action))
                        }
                        Button(
                            onClick = { showRejectSheet = true },
                            enabled = actionState !is AdminActionState.Loading,
                            modifier = Modifier.fillMaxWidth(),
                            colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.error,
                                contentColor = Color.White
                            )
                        ) {
                            Text(stringResource(R.string.reject_action))
                        }
                    }
                }
            }

            OutlinedButton(onClick = onBack, modifier = Modifier.fillMaxWidth()) {
                Text(stringResource(R.string.back))
            }
        }
    }

    if (showApproveConfirm) {
        AlertDialog(
            onDismissRequest = { showApproveConfirm = false },
            title = { Text(stringResource(R.string.approve_confirm_title)) },
            text = { Text(stringResource(R.string.approve_confirm_message)) },
            dismissButton = {
                OutlinedButton(onClick = { showApproveConfirm = false }) {
                    Text(stringResource(R.string.cancel))
                }
            },
            confirmButton = {
                Button(onClick = {
                    showApproveConfirm = false
                    viewModel.approve()
                }) {
                    Text(stringResource(R.string.approve_action))
                }
            }
        )
    }

    if (showRejectSheet) {
        RejectApplicationBottomSheet(
            onDismiss = { showRejectSheet = false },
            onReject = {
                showRejectSheet = false
                viewModel.reject(it)
            }
        )
    }

    if (showReturnSheet) {
        ReturnApplicationBottomSheet(
            onDismiss = { showReturnSheet = false },
            onReturn = {
                showReturnSheet = false
                viewModel.returnForRevision(it)
            }
        )
    }
}

@Composable
private fun DetailContent(
    application: ApplicationDetail,
    onDownloadDocument: (String, String) -> Unit
) {
    ApplicationStatusBadge(status = application.status)

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
    application.additionalInfo?.let { DetailField(stringResource(R.string.label_additional_short), it) }
    application.rejectionReason?.let { DetailField(stringResource(R.string.admin_comment), it) }

    Text(stringResource(R.string.documents_section), style = MaterialTheme.typography.titleMedium)
    if (application.documents.isEmpty()) {
        Text(stringResource(R.string.no_documents), style = MaterialTheme.typography.bodyMedium)
    } else {
        application.documents.forEach { document ->
            DocumentItem(
                name = document.originalFilename ?: document.filename,
                sizeBytes = document.fileSize,
                onDownload = { onDownloadDocument(document.id, document.filename) }
            )
        }
    }
}

@Composable
private fun DetailField(label: String, value: String) {
    Column {
        Text(label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.outline)
        Text(value, style = MaterialTheme.typography.bodyLarge)
    }
}
