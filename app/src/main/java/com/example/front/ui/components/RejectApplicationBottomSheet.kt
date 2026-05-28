package com.example.front.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.front.R

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun RejectApplicationBottomSheet(
    onDismiss: () -> Unit,
    onReject: (String) -> Unit
) {
    var reason by remember { mutableStateOf("") }
    val trimmed = reason.trim()
    val isValid = trimmed.length in 10..500

    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 12.dp)
        ) {
            Text(stringResource(R.string.reject_title), style = MaterialTheme.typography.titleLarge)

            FormTextField(
                value = reason,
                onValueChange = { reason = it.take(500) },
                label = { Text(stringResource(R.string.reject_reason_label)) },
                supportingText = {
                    Text(stringResource(R.string.reason_length, reason.length, 500))
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
                singleLine = false,
                minLines = 4
            )

            OutlinedButton(
                onClick = onDismiss,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
            ) {
                Text(stringResource(R.string.cancel))
            }

            Button(
                onClick = { onReject(trimmed) },
                enabled = isValid,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp, bottom = 16.dp)
            ) {
                Text(stringResource(R.string.reject_action))
            }
        }
    }
}
