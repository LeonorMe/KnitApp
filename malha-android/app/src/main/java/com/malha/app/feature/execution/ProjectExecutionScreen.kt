package com.malha.app.feature.execution

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.malha.app.domain.model.Pattern
import com.malha.app.domain.model.PatternStep
import com.malha.app.domain.model.Project
import com.malha.app.core.design.component.AidiCompanion
import com.malha.app.core.design.component.ImagePlaceholder
import com.malha.app.core.design.component.StitchLinkText

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
    var showNoteDialog by remember { mutableStateOf(false) }
    var selectedStitch by remember { mutableStateOf<String?>(null) }
    
    val configuration = LocalConfiguration.current
    val isTablet = configuration.screenWidthDp > 600

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

            if (isTablet) {
                Row(
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    Column(
                        modifier = Modifier.weight(1f).verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(18.dp)
                    ) {
                        ProjectInfoCard(project)
                        ExecutionProgress(uiState)
                        if (uiState.pattern?.availableSizes?.isNotEmpty() == true) {
                            SizeSelector(
                                sizes = uiState.pattern.availableSizes,
                                selectedSize = uiState.selectedSize,
                                onSizeSelected = viewModel::selectSize
                            )
                        }
                        StepCard(currentStep) { selectedStitch = it }
                    }
                    Column(
                        modifier = Modifier.weight(0.8f).verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(18.dp)
                    ) {
                        AidiCompanion(modifier = Modifier.fillMaxWidth())
                        NoteCard(uiState) { showNoteDialog = true }
                        Controls(uiState, viewModel)
                    }
                }
            } else {
                Column(
                    modifier = Modifier.weight(1f).verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(18.dp)
                ) {
                    ProjectInfoCard(project)
                    ExecutionProgress(uiState)
                    if (uiState.pattern?.availableSizes?.isNotEmpty() == true) {
                        SizeSelector(
                            sizes = uiState.pattern.availableSizes,
                            selectedSize = uiState.selectedSize,
                            onSizeSelected = viewModel::selectSize
                        )
                    }
                    StepCard(currentStep) { selectedStitch = it }
                    NoteCard(uiState) { showNoteDialog = true }
                    AidiCompanion(modifier = Modifier.size(100.dp))
                    Controls(uiState, viewModel)
                }
            }
        }
    }

    if (showNoteDialog) {
        StepNoteDialog(
            initialNote = uiState.currentNote.orEmpty(),
            onDismiss = { showNoteDialog = false },
            onSave = { note ->
                viewModel.saveCurrentStepNote(note)
                showNoteDialog = false
            }
        )
    }
    
    if (selectedStitch != null) {
        StitchDetailDialog(
            stitchName = selectedStitch!!,
            onDismiss = { selectedStitch = null }
        )
    }
}

@Composable
private fun ProjectInfoCard(project: Project?) {
    if (project == null) return
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            ImagePlaceholder(label = project.name, imageUri = project.imageUri)
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = "Project Details",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = project.name,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun ExecutionProgress(uiState: ProjectExecutionUiState) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        LinearProgressIndicator(
            progress = { (uiState.project?.progressPercent ?: 0) / 100f },
            modifier = Modifier.fillMaxWidth()
        )
        Text(
            text = buildString {
                append("Step ${uiState.currentStepNumber} of ${uiState.totalSteps.coerceAtLeast(1)}")
                if (uiState.isCurrentStepDone) {
                    append(" - done")
                }
            },
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}

@Composable
private fun StepCard(currentStep: PatternStep?, onStitchClick: (String) -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
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
            
            StitchLinkText(
                text = currentStep?.instruction ?: "No pattern step assigned yet.",
                knownStitches = listOf("K2tog", "SSK", "YO", "M1L", "M1R", "Knit", "Purl", "Cast on"),
                onStitchClick = onStitchClick
            )
            
            if (currentStep?.stitchCount != null) {
                Text(
                    text = "Ending count: ${currentStep.stitchCount} sts",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun NoteCard(uiState: ProjectExecutionUiState, onEditNote: () -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Step note",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = uiState.currentNote ?: "No note for this step yet.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            TextButton(
                onClick = onEditNote,
                enabled = uiState.currentStep != null
            ) {
                Text(if (uiState.currentNote == null) "Add note" else "Edit note")
            }
        }
    }
}

@Composable
private fun Controls(uiState: ProjectExecutionUiState, viewModel: ProjectExecutionViewModel) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
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
            enabled = uiState.currentStep != null,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (uiState.isCurrentStepDone) "Keep Done" else "Mark step done")
        }
    }
}

@Composable
private fun StitchDetailDialog(stitchName: String, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stitchName) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "This is a brief explanation of how to perform the $stitchName stitch. In the full version, this will include images and video links from the Stitch Library.",
                    style = MaterialTheme.typography.bodyMedium
                )
                ImagePlaceholder(label = stitchName)
            }
        },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text("Got it")
            }
        }
    )
}

@Composable
private fun StepNoteDialog(
    initialNote: String,
    onDismiss: () -> Unit,
    onSave: (String) -> Unit
) {
    var note by remember(initialNote) { mutableStateOf(initialNote) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Step note") },
        text = {
            OutlinedTextField(
                value = note,
                onValueChange = { note = it },
                label = { Text("Personal note") },
                minLines = 4
            )
        },
        confirmButton = {
            Button(onClick = { onSave(note) }) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
private fun SizeSelector(
    sizes: List<String>,
    selectedSize: String?,
    onSizeSelected: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = "Select Size",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            sizes.forEach { size ->
                val isSelected = size == selectedSize
                OutlinedButton(
                    onClick = { onSizeSelected(size) },
                    modifier = Modifier.weight(1f),
                    colors = if (isSelected) {
                        androidx.compose.material3.ButtonDefaults.outlinedButtonColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    } else {
                        androidx.compose.material3.ButtonDefaults.outlinedButtonColors()
                    }
                ) {
                    Text(size)
                }
            }
        }
    }
}
