package com.malha.app.feature.execution

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun ProjectExecutionScreen(
    projectId: String,
    viewModel: ProjectExecutionViewModel = viewModel(
        factory = ProjectExecutionViewModel.factory(projectId)
    )
) {
    val uiState by viewModel.uiState.collectAsState()
    val project = uiState.project
    val currentStep = uiState.currentStep

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 28.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            Text(
                text = project?.name ?: "Project",
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.onBackground
            )

            if (uiState.isLoading) {
                Text(
                    text = "Loading project...",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                return@Column
            }

            if (project == null) {
                Text(
                    text = "Project not found.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.error
                )
                return@Column
            }

            if (uiState.errorMessage != null) {
                Text(
                    text = uiState.errorMessage.orEmpty(),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.error
                )
            }

            LinearProgressIndicator(
                progress = { project.progressPercent / 100f },
                modifier = Modifier.fillMaxWidth()
            )

            Text(
                text = "Step ${uiState.currentStepNumber} of ${uiState.totalSteps.coerceAtLeast(1)}",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onBackground
            )

            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    if (currentStep?.rowNumber != null) {
                        Text(
                            text = "Row ${currentStep.rowNumber}",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                    Text(
                        text = currentStep?.instruction ?: "No pattern step assigned yet.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = viewModel::previousStep,
                    enabled = uiState.canGoPrevious,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Previous")
                }
                Button(
                    onClick = viewModel::nextStep,
                    enabled = uiState.canGoNext,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Next")
                }
            }

            Button(
                onClick = viewModel::markCurrentStepDone,
                enabled = currentStep != null,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Mark step done")
            }
        }
    }
}

