package com.example.front.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.front.domain.model.ApplicationStatus

@Composable
fun ApplicationStatusBadge(
    status: ApplicationStatus,
    modifier: Modifier = Modifier
) {
    val (label, color) = when (status) {
        ApplicationStatus.NEW -> "Новая" to MaterialTheme.colorScheme.primary
        ApplicationStatus.REVIEWING -> "На проверке" to MaterialTheme.colorScheme.tertiary
        ApplicationStatus.APPROVED -> "Одобрена" to Color(0xFF2E7D32)
        ApplicationStatus.REJECTED -> "Отклонена" to MaterialTheme.colorScheme.error
        ApplicationStatus.RETURNED -> "На доработке" to Color(0xFFF57C00)
    }

    Text(
        text = label,
        color = Color.White,
        style = MaterialTheme.typography.labelMedium,
        modifier = modifier
            .background(color, RoundedCornerShape(50))
            .padding(horizontal = 12.dp, vertical = 6.dp)
    )
}
