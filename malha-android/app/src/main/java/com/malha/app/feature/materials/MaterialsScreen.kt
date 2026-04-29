package com.malha.app.feature.materials

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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.text.KeyboardOptions
import androidx.lifecycle.viewmodel.compose.viewModel
import com.malha.app.core.design.component.ListScreen

@Composable
fun MaterialsScreen(viewModel: MaterialsViewModel = viewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    var showCreateDialog by remember { mutableStateOf(false) }

    ListScreen(
        title = "Materials",
        subtitle = "Track yarn, needles, hooks, purchases, and project assignments.",
        items = uiState.materials.map { material ->
            "${material.name} - ${material.quantity} ${material.unit}"
        },
        emptyText = if (uiState.isLoading) "Loading materials..." else "No materials yet.",
        errorMessage = uiState.errorMessage,
        actionLabel = "New yarn",
        onActionClick = {
            viewModel.clearError()
            showCreateDialog = true
        }
    )

    if (showCreateDialog) {
        CreateYarnDialog(
            isCreating = uiState.isCreating,
            onDismiss = { showCreateDialog = false },
            onCreate = { name, quantity, unit ->
                viewModel.createYarn(name, quantity, unit)
                showCreateDialog = false
            }
        )
    }
}

@Composable
private fun CreateYarnDialog(
    isCreating: Boolean,
    onDismiss: () -> Unit,
    onCreate: (String, String, String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var quantity by remember { mutableStateOf("") }
    var unit by remember { mutableStateOf("skeins") }
    var localError by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("New yarn") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = {
                        name = it
                        localError = null
                    },
                    label = { Text("Yarn name") },
                    singleLine = true
                )
                OutlinedTextField(
                    value = quantity,
                    onValueChange = {
                        quantity = it
                        localError = null
                    },
                    label = { Text("Quantity") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true
                )
                OutlinedTextField(
                    value = unit,
                    onValueChange = { unit = it },
                    label = { Text("Unit") },
                    supportingText = {
                        if (localError != null) {
                            Text(localError.orEmpty())
                        }
                    },
                    isError = localError != null,
                    singleLine = true
                )
            }
        },
        confirmButton = {
            Button(
                enabled = !isCreating,
                onClick = {
                    localError = when {
                        name.isBlank() -> "Yarn name is required."
                        quantity.replace(',', '.').toDoubleOrNull() == null -> "Quantity must be a number."
                        else -> null
                    }
                    if (localError == null) {
                        onCreate(name, quantity, unit)
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
