package com.example.front.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.front.R
import com.example.front.domain.model.ApplicationDetail
import com.example.front.utils.DateFormatter

@Composable
fun AdminApplicationListItem(
    application: ApplicationDetail,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(application.fullName, style = MaterialTheme.typography.titleMedium)
                Text(
                    text = application.email,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.outline
                )
                Text(
                    text = stringResource(R.string.application_number, DateFormatter.shortId(application.id)),
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = stringResource(R.string.submitted_at, DateFormatter.formatIso(application.createdAt)),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.outline
                )
                Text(
                    text = stringResource(R.string.updated_at, DateFormatter.formatIso(application.updatedAt)),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.outline
                )
                if (application.documents.isNotEmpty()) {
                    Text(
                        text = stringResource(R.string.documents_count, application.documents.size),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
            ApplicationStatusBadge(status = application.status)
        }
    }
}
