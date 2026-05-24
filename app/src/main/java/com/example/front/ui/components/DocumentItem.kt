package com.example.front.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.front.R

@Composable
fun DocumentItem(
    name: String,
    sizeBytes: Long,
    onRemove: (() -> Unit)? = null,
    onDownload: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = fileIconFor(name),
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier
                .size(40.dp)
                .padding(end = 12.dp)
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(name, style = MaterialTheme.typography.bodyMedium)
            Text(
                text = formatFileSize(sizeBytes),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.outline
            )
        }
        if (onDownload != null) {
            IconButton(onClick = onDownload) {
                Text(stringResource(R.string.download))
            }
        }
        if (onRemove != null) {
            IconButton(onClick = onRemove) {
                Text(stringResource(R.string.delete))
            }
        }
    }
}

private fun fileIconFor(name: String): String = when (name.substringAfterLast('.').lowercase()) {
    "pdf" -> "PDF"
    "jpg", "jpeg", "png" -> "IMG"
    else -> "DOC"
}

private fun formatFileSize(sizeBytes: Long): String {
    val megabytes = sizeBytes / (1024f * 1024f)
    return "%.1f MB".format(megabytes)
}
