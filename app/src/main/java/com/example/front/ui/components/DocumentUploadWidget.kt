package com.example.front.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.front.utils.Constants

data class UploadDocumentUi(
    val name: String,
    val sizeBytes: Long
)

@Composable
fun DocumentUploadWidget(
    documents: List<UploadDocumentUi>,
    onAddDocument: () -> Unit,
    onRemoveDocument: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text("Загруженные документы", style = MaterialTheme.typography.titleMedium)

        documents.forEachIndexed { index, document ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(document.name, style = MaterialTheme.typography.bodyMedium)
                    Text(formatFileSize(document.sizeBytes), style = MaterialTheme.typography.bodySmall)
                }
                IconButton(onClick = { onRemoveDocument(index) }) {
                    Text("Удалить")
                }
            }
        }

        Button(
            onClick = onAddDocument,
            enabled = documents.size < Constants.MAX_DOCUMENTS,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Добавить документ")
        }
    }
}

private fun formatFileSize(sizeBytes: Long): String {
    val megabytes = sizeBytes / (1024f * 1024f)
    return "%.1f MB".format(megabytes)
}
