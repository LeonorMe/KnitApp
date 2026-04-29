package com.malha.app.feature.projects

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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.malha.app.core.design.component.ListScreen

@Composable
fun ProjectsScreen(viewModel: ProjectsViewModel = viewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    var showCreateDialog by remember { mutableStateOf(false) }

    ListScreen(
        title = "Projects",
        subtitle = "Create projects, resume the current step, and track handmade progress.",
        items = uiState.projects.map { project ->
            "${project.name} - step ${project.currentStepIndex + 1}"
        },
        emptyText = if (uiState.isLoading) "Loading projects..." else "No projects yet.",
        errorMessage = uiState.errorMessage,
        actionLabel = "New project",
        onActionClick = {
            viewModel.clearError()
            showCreateDialog = true
        }
    )

    if (showCreateDialog) {
        CreateProjectDialog(
            isCreating = uiState.isCreating,
            onDismiss = { showCreateDialog = false },
            onCreate = { name ->
                viewModel.createProject(name)
                showCreateDialog = false
            }
        )
    }
}

@Composable
private fun CreateProjectDialog(
    isCreating: Boolean,
    onDismiss: () -> Unit,
    onCreate: (String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var localError by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("New project") },
        text = {
            OutlinedTextField(
                value = name,
                onValueChange = {
                    name = it
                    localError = null
                },
                label = { Text("Project name") },
                supportingText = {
                    if (localError != null) {
                        Text(localError.orEmpty())
                    }
                },
                isError = localError != null,
                singleLine = true
            )
        },
        confirmButton = {
            Button(
                enabled = !isCreating,
                onClick = {
                    if (name.isBlank()) {
                        localError = "Project name is required."
                    } else {
                        onCreate(name)
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
