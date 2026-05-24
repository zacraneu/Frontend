package com.example.front.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.front.R
import com.example.front.domain.model.ApplicationStatus

@Composable
fun ApplicationStatusBadge(
    status: ApplicationStatus,
    modifier: Modifier = Modifier
) {
    val (labelRes, color) = when (status) {
        ApplicationStatus.NEW -> R.string.status_new to MaterialTheme.colorScheme.primary
        ApplicationStatus.REVIEWING -> R.string.status_reviewing to MaterialTheme.colorScheme.tertiary
        ApplicationStatus.APPROVED -> R.string.status_approved to Color(0xFF2E7D32)
        ApplicationStatus.REJECTED -> R.string.status_rejected to MaterialTheme.colorScheme.error
        ApplicationStatus.RETURNED -> R.string.status_returned to Color(0xFFF57C00)
    }

    Text(
        text = stringResource(labelRes),
        color = Color.White,
        style = MaterialTheme.typography.labelMedium,
        modifier = modifier
            .background(color, RoundedCornerShape(50))
            .padding(horizontal = 12.dp, vertical = 6.dp)
    )
}
