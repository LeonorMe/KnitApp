package com.malha.app.feature.patterns.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun PatternDetailScreen(
    patternId: String,
    onProjectStarted: (String) -> Unit,
    viewModel: PatternDetailViewModel = viewModel(
        factory = PatternDetailViewModel.factory(patternId)
    )
) {
    val uiState by viewModel.uiState.collectAsState()
    val pattern = uiState.pattern
    var showStartDialog by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.startedProjectId) {
        uiState.startedProjectId?.let(onProjectStarted)
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        LazyColumn(
            modifier = Modifier.padding(PaddingValues(horizontal = 24.dp, vertical = 28.dp)),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Text(
                    text = pattern?.title ?: "Pattern",
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            if (uiState.isLoading) {
                item {
                    Text(
                        text = "Loading pattern...",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                return@LazyColumn
            }

            if (pattern == null) {
                item {
                    Text(
                        text = "Pattern not found.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.error
                    )
                }
                return@LazyColumn
            }

            if (uiState.errorMessage != null) {
                item {
                    Text(
                        text = uiState.errorMessage.orEmpty(),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }

            item {
                Button(
                    onClick = {
                        viewModel.clearError()
                        showStartDialog = true
                    },
                    enabled = !uiState.isStartingProject,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Start project from this pattern")
                }
            }

            item {
                Text(
                    text = "${pattern.allSteps.size} steps",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            items(pattern.allSteps) { step ->
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = if (step.rowNumber != null) {
                                "Row ${step.rowNumber}"
                            } else {
                                "Step ${step.orderIndex + 1}"
                            },
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.secondary
                        )
                        Text(
                            text = step.instruction,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }
    }

    if (showStartDialog && pattern != null) {
        StartProjectDialog(
            initialName = pattern.title,
            isStarting = uiState.isStartingProject,
            onDismiss = { showStartDialog = false },
            onStart = { projectName ->
                viewModel.startProject(projectName)
                showStartDialog = false
            }
        )
    }
}

@Composable
private fun StartProjectDialog(
    initialName: String,
    isStarting: Boolean,
    onDismiss: () -> Unit,
    onStart: (String) -> Unit
) {
    var projectName by remember(initialName) { mutableStateOf(initialName) }
    var localError by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Start project") },
        text = {
            OutlinedTextField(
                value = projectName,
                onValueChange = {
                    projectName = it
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
                enabled = !isStarting,
                onClick = {
                    if (projectName.isBlank()) {
                        localError = "Project name is required."
                    } else {
                        onStart(projectName)
                    }
                }
            ) {
                Text("Start")
            }
        },
        dismissButton = {
            TextButton(
                enabled = !isStarting,
                onClick = onDismiss
            ) {
                Text("Cancel")
            }
        }
    )
}

