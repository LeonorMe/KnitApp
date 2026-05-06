package com.malha.app.feature.patterns

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.malha.app.core.design.component.ListScreen

@Composable
fun PatternsScreen(
    onOpenPattern: (String) -> Unit,
    viewModel: PatternsViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showCreateDialog by remember { mutableStateOf(false) }

    ListScreen(
        title = "Patterns",
        subtitle = "Manual pattern input and the first starter patterns will live here.",
        items = uiState.patterns.map { pattern ->
            "${pattern.title} - ${pattern.steps.size} steps"
        },
        emptyText = if (uiState.isLoading) "Loading patterns..." else "No patterns yet.",
        errorMessage = uiState.errorMessage,
        actionLabel = "New pattern",
        onActionClick = {
            viewModel.clearError()
            showCreateDialog = true
        },
        onItemClick = { index ->
            uiState.patterns.getOrNull(index)?.let { pattern ->
                onOpenPattern(pattern.id)
            }
        }
    )

    if (showCreateDialog) {
        CreatePatternDialog(
            isCreating = uiState.isCreating,
            onDismiss = { showCreateDialog = false },
            onCreate = { title, instructions ->
                viewModel.createPattern(title, instructions)
                showCreateDialog = false
            }
        )
    }
}

@Composable
private fun CreatePatternDialog(
    isCreating: Boolean,
    onDismiss: () -> Unit,
    onCreate: (String, String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var instructions by remember { mutableStateOf("") }
    var localError by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("New pattern") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = {
                        title = it
                        localError = null
                    },
                    label = { Text("Pattern title") },
                    singleLine = true
                )
                OutlinedTextField(
                    value = instructions,
                    onValueChange = {
                        instructions = it
                        localError = null
                    },
                    label = { Text("Instructions") },
                    supportingText = {
                        Text(localError ?: "Write one instruction per line.")
                    },
                    isError = localError != null,
                    minLines = 4
                )
            }
        },
        confirmButton = {
            Button(
                enabled = !isCreating,
                onClick = {
                    localError = when {
                        title.isBlank() -> "Pattern title is required."
                        instructions.isBlank() -> "Add at least one instruction."
                        else -> null
                    }
                    if (localError == null) {
                        onCreate(title, instructions)
                    }
                }
            ) {
                Text("Create")
            }
        },
        dismissButton = {
            Button(
                enabled = !isCreating,
                onClick = onDismiss
            ) {
                Text("Cancel")
            }
        }
    )
}
