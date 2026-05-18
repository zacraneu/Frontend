package com.example.front.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ApplicationListScreen(
    onCreateApplication: () -> Unit,
    onOpenApplication: (String) -> Unit,
    onOpenProfile: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Заявки", style = MaterialTheme.typography.headlineMedium)
            OutlinedButton(onClick = onOpenProfile) {
                Text("Профиль")
            }
        }

        Text(
            text = "Список заявок будет подключён к GET /api/v1/applications на следующем этапе.",
            style = MaterialTheme.typography.bodyMedium
        )

        Button(onClick = onCreateApplication, modifier = Modifier.fillMaxWidth()) {
            Text("Новая заявка")
        }

        OutlinedButton(
            onClick = { onOpenApplication("demo-application") },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Открыть демо-детали")
        }
    }
}
