package com.malha.app.feature.execution

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import com.malha.app.core.design.component.StitchLinkText
import com.malha.app.core.design.component.StitchDetailDialog
import com.malha.app.core.design.component.KNOWN_STITCHES

@Composable
fun ProjectExecutionScreen(
    projectId: String,
    viewModel: ProjectExecutionViewModel = viewModel(
        factory = ProjectExecutionViewModel.factory(projectId)
    )
) {
    val uiState = viewModel.uiState.collectAsState().value
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
                .padding(horizontal = 24.dp, vertical = 18.dp)
                .fillMaxSize()
        ) {
            Text(
                text = "Execution",
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(bottom = 16.dp)
            )

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
                        val pattern = uiState.pattern
                        if (pattern != null && pattern.availableSizes.isNotEmpty()) {
                            SizeSelector(
                                sizes = pattern.availableSizes,
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
                    val pattern = uiState.pattern
                    if (pattern != null && pattern.availableSizes.isNotEmpty()) {
                        SizeSelector(
                            sizes = pattern.availableSizes,
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
            currentNote = uiState.currentStepProgress?.note.orEmpty(),
            onDismiss = { showNoteDialog = false },
            onSave = { note ->
                // viewModel.saveNote(note) // TODO: Implement
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
private fun ProjectInfoCard(project: Project) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(project.name, style = MaterialTheme.typography.titleLarge)
            Text("Knitting companion active", style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
private fun ExecutionProgress(uiState: ProjectExecutionUiState) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                "Progress: ${uiState.project?.progressPercent}%",
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                "Step ${uiState.currentStepNumber} of ${uiState.totalSteps}",
                style = MaterialTheme.typography.bodyLarge
            )
        }
        LinearProgressIndicator(
            progress = { (uiState.project?.progressPercent ?: 0) / 100f },
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun StepCard(currentStep: PatternStep?, onStitchClick: (String) -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("Current Instruction", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
            if (currentStep != null) {
                StitchLinkText(
                    text = currentStep.instructionData,
                    knownStitches = KNOWN_STITCHES,
                    onStitchClick = onStitchClick
                )
                if (currentStep.stitchCountData != null) {
                    Text(
                        "Target: ${currentStep.stitchCountData} stitches",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            } else {
                Text("All steps completed!", style = MaterialTheme.typography.headlineSmall)
            }
        }
    }
}

@Composable
private fun NoteCard(uiState: ProjectExecutionUiState, onEditNote: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onEditNote() },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("My Notes", style = MaterialTheme.typography.titleMedium)
            Text(
                text = uiState.currentStepProgress?.note ?: "No notes for this step. Click to add one.",
                style = MaterialTheme.typography.bodyMedium,
                color = if (uiState.currentStepProgress?.note == null) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
private fun Controls(uiState: ProjectExecutionUiState, viewModel: ProjectExecutionViewModel) {
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
            onClick = viewModel::markCurrentStepDone,
            enabled = uiState.canGoNext,
            modifier = Modifier.weight(1.5f)
        ) {
            Text(if (uiState.currentStepProgress?.isDone == true) "Next Step" else "Mark Done")
        }
    }
}

@Composable
private fun StepNoteDialog(
    currentNote: String,
    onDismiss: () -> Unit,
    onSave: (String) -> Unit
) {
    var note by remember { mutableStateOf(currentNote) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Step Note") },
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
