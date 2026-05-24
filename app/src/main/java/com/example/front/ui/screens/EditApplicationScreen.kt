package com.example.front.ui.screens

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.front.R
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.front.ui.components.DocumentUploadWidget
import com.example.front.ui.components.FormTextField
import com.example.front.ui.components.LoadingIndicator
import com.example.front.ui.components.PhoneTextField
import com.example.front.utils.Constants
import com.example.front.viewmodel.EditApplicationViewModel
import com.example.front.viewmodel.SubmitState
import java.io.File

private val DOCUMENT_MIME_TYPES = arrayOf(
    "application/pdf",
    "image/jpeg",
    "image/png"
)

@Composable
fun EditApplicationScreen(
    onBack: () -> Unit,
    onSubmitted: () -> Unit,
    viewModel: EditApplicationViewModel = hiltViewModel()
) {
    val formState by viewModel.formState.collectAsState()
    val submitState by viewModel.submitState.collectAsState()
    val loadError by viewModel.loadError.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    var cameraUri by remember { mutableStateOf<Uri?>(null) }

    val documentPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri -> uri?.let(viewModel::addDocument) }

    val multipleDocumentsPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenMultipleDocuments()
    ) { uris -> uris.forEach(viewModel::addDocument) }

    val takePictureLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) cameraUri?.let(viewModel::addDocument)
        cameraUri = null
    }

    LaunchedEffect(submitState) {
        when (val state = submitState) {
            is SubmitState.Error -> {
                snackbarHostState.showSnackbar(state.message)
                Toast.makeText(context, state.message, Toast.LENGTH_SHORT).show()
                viewModel.resetSubmitState()
            }
            is SubmitState.Success -> {
                Toast.makeText(context, R.string.application_resubmitted, Toast.LENGTH_SHORT).show()
                viewModel.resetSubmitState()
                onSubmitted()
            }
            else -> Unit
        }
    }

    Scaffold(snackbarHost = { SnackbarHost(snackbarHostState) }) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = stringResource(R.string.edit_application_title),
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
            )

            Text(
                text = stringResource(R.string.edit_application_hint),
                style = MaterialTheme.typography.bodyMedium
            )

            loadError?.let {
                Text(text = it, color = MaterialTheme.colorScheme.error)
            }

            if (formState.isProfileLoading) {
                LoadingIndicator()
            }

            FormTextField(
                value = formState.fullName,
                onValueChange = viewModel::updateFullName,
                label = { Text(stringResource(R.string.full_name)) },
                modifier = Modifier.fillMaxWidth(),
                enabled = !formState.isProfileLoading && submitState !is SubmitState.Loading
            )
            FormTextField(
                value = formState.email,
                onValueChange = viewModel::updateEmail,
                label = { Text(stringResource(R.string.email)) },
                modifier = Modifier.fillMaxWidth(),
                enabled = !formState.isProfileLoading && submitState !is SubmitState.Loading
            )
            PhoneTextField(
                phoneDigits = formState.phone,
                onPhoneDigitsChange = viewModel::updatePhone,
                modifier = Modifier.fillMaxWidth(),
                enabled = !formState.isProfileLoading && submitState !is SubmitState.Loading
            )
            FormTextField(
                value = formState.submissionReason,
                onValueChange = viewModel::updateSubmissionReason,
                label = { Text(stringResource(R.string.submission_reason)) },
                supportingText = {
                    Text(
                        stringResource(
                            R.string.reason_length,
                            formState.submissionReason.length,
                            Constants.MAX_SUBMISSION_REASON_LENGTH
                        )
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = false,
                minLines = 3,
                enabled = submitState !is SubmitState.Loading
            )
            FormTextField(
                value = formState.additionalInfo,
                onValueChange = viewModel::updateAdditionalInfo,
                label = { Text(stringResource(R.string.additional_info)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = false,
                minLines = 2,
                enabled = submitState !is SubmitState.Loading
            )

            DocumentUploadWidget(
                documents = formState.documents,
                onPickFromGallery = { documentPickerLauncher.launch(DOCUMENT_MIME_TYPES) },
                onPickMultiple = { multipleDocumentsPickerLauncher.launch(DOCUMENT_MIME_TYPES) },
                onTakePhoto = {
                    val photoFile = File(context.cacheDir, "camera_${System.currentTimeMillis()}.jpg")
                    val uri = FileProvider.getUriForFile(
                        context,
                        "${context.packageName}.fileprovider",
                        photoFile
                    )
                    cameraUri = uri
                    takePictureLauncher.launch(uri)
                },
                onRemoveDocument = viewModel::removeDocument,
                enabled = submitState !is SubmitState.Loading,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            Button(
                onClick = viewModel::resubmitApplication,
                enabled = submitState !is SubmitState.Loading && !formState.isProfileLoading,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (submitState is SubmitState.Loading) {
                    CircularProgressIndicator(modifier = Modifier.padding(end = 8.dp), strokeWidth = 2.dp)
                }
                Text(stringResource(R.string.resubmit))
            }

            OutlinedButton(
                onClick = onBack,
                enabled = submitState !is SubmitState.Loading,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp)
            ) {
                Text(stringResource(R.string.back))
            }
        }
    }
}
