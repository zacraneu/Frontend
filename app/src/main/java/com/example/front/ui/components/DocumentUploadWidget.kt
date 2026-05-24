package com.example.front.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.front.R
import com.example.front.domain.model.DocumentUpload
import com.example.front.utils.Constants

@Composable
fun DocumentUploadWidget(
    documents: List<DocumentUpload>,
    onPickFromGallery: () -> Unit,
    onPickMultiple: () -> Unit,
    onTakePhoto: () -> Unit,
    onRemoveDocument: (Int) -> Unit,
    enabled: Boolean = true,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = stringResource(R.string.documents_title, Constants.MAX_DOCUMENTS),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        documents.forEachIndexed { index, document ->
            DocumentItem(
                name = document.name,
                sizeBytes = document.file.length(),
                onRemove = { onRemoveDocument(index) }
            )
        }

        OutlinedButton(
            onClick = onPickFromGallery,
            enabled = enabled && documents.size < Constants.MAX_DOCUMENTS,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(R.string.add_file))
        }

        OutlinedButton(
            onClick = onPickMultiple,
            enabled = enabled && documents.size < Constants.MAX_DOCUMENTS,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
        ) {
            Text(stringResource(R.string.add_multiple_files))
        }

        OutlinedButton(
            onClick = onTakePhoto,
            enabled = enabled && documents.size < Constants.MAX_DOCUMENTS,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
        ) {
            Text(stringResource(R.string.take_photo))
        }

        if (documents.size >= Constants.MAX_DOCUMENTS) {
            Text(
                text = stringResource(R.string.max_documents_reached),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}
