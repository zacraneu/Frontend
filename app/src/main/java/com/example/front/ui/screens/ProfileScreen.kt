package com.example.front.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.front.ui.components.LoadingIndicator
import com.example.front.viewmodel.ProfileUiState
import com.example.front.viewmodel.ProfileViewModel

@Composable
fun ProfileScreen(
    onBack: () -> Unit,
    onLogout: () -> Unit,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    LaunchedEffect(uiState) {
        if (uiState is ProfileUiState.Error) {
            val message = (uiState as ProfileUiState.Error).message
            snackbarHostState.showSnackbar(message)
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Профиль", style = MaterialTheme.typography.headlineMedium)

            when (val state = uiState) {
                ProfileUiState.Loading -> LoadingIndicator()
                is ProfileUiState.Success -> ProfileContent(user = state.user)
                is ProfileUiState.Error -> {
                    state.cachedUser?.let { ProfileContent(user = it) }
                    Text(
                        text = state.message,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    OutlinedButton(
                        onClick = viewModel::loadProfile,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Повторить")
                    }
                }
            }

            Button(
                onClick = { viewModel.logout(onLogout) },
                modifier = Modifier.fillMaxWidth(),
                enabled = uiState !is ProfileUiState.Loading
            ) {
                Text("Выход")
            }

            OutlinedButton(onClick = onBack, modifier = Modifier.fillMaxWidth()) {
                Text("Назад")
            }
        }
    }
}

@Composable
private fun ProfileContent(
    user: com.example.front.domain.model.User
) {
    Text("ФИО: ${user.fullName}", style = MaterialTheme.typography.bodyLarge)
    Text("Email: ${user.email}", style = MaterialTheme.typography.bodyMedium)
    user.phone?.let {
        Text("Телефон: $it", style = MaterialTheme.typography.bodyMedium)
    }
    user.registeredAt?.let {
        Text("Дата регистрации: $it", style = MaterialTheme.typography.bodySmall)
    }
    Text(
        text = if (user.isVerified) "Статус: подтверждён" else "Статус: не подтверждён",
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.outline
    )
}
