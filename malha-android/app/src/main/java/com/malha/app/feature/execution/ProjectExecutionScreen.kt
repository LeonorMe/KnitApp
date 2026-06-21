package com.malha.app.feature.execution

import android.app.Activity
import android.view.WindowManager
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.malha.app.core.design.component.AidiCompanion
import com.malha.app.core.design.component.KNOWN_STITCHES
import com.malha.app.core.design.component.StitchDetailDialog
import com.malha.app.core.design.component.StitchLinkText
import com.malha.app.domain.model.PatternStep
import com.malha.app.R
import androidx.compose.ui.res.stringResource

// ------ Theme enum for quick toggle ------
enum class ExecutionTheme { DARK, LIGHT, WARM }

@Composable
fun ProjectExecutionScreen(
    projectId: String,
    onBack: () -> Unit, //(() -> Unit)? = null,
    onOpenSettings: (String) -> Unit,
    viewModel: ProjectExecutionViewModel = viewModel(
        factory = ProjectExecutionViewModel.factory(projectId)
    )
) {
    val uiState = viewModel.uiState.collectAsState().value
    val project = uiState.project
    var showNoteDialog by remember { mutableStateOf(false) }
    var selectedStitch by remember { mutableStateOf<String?>(null) }
    var showCongratsDialog by remember { mutableStateOf(false) }
    var rowCounter by remember { mutableStateOf(0) }

    // Quick theme toggle (purely local to execution screen for now)
    var executionTheme by remember { mutableStateOf(ExecutionTheme.DARK) }

    // Keep screen on toggle
    var keepScreenOn by remember { mutableStateOf(true) }
    val context = LocalContext.current
    val activity = context as? Activity
    DisposableEffect(keepScreenOn) {
        if (keepScreenOn) {
            activity?.window?.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        } else {
            activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }
        onDispose {
            activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }
    }

    // Derive background/surface from execution theme
    val bgColor = when (executionTheme) {
        ExecutionTheme.DARK -> Color(0xFF1A1A2E)
        ExecutionTheme.LIGHT -> MaterialTheme.colorScheme.background
        ExecutionTheme.WARM -> Color(0xFF2C1A0E)
    }
    val onBgColor = when (executionTheme) {
        ExecutionTheme.DARK -> Color(0xFFE8E8F0)
        ExecutionTheme.LIGHT -> MaterialTheme.colorScheme.onBackground
        ExecutionTheme.WARM -> Color(0xFFF5E6D3)
    }

    val configuration = LocalConfiguration.current
    val isTablet = configuration.screenWidthDp > 600

    // Check if all steps finished to show congratulations
    val allDone = !uiState.isLoading && uiState.totalSteps > 0 &&
            (project?.currentStepIndex ?: 0) >= uiState.totalSteps - 1 &&
            uiState.isCurrentStepDone

    LaunchedEffect(allDone) {
        if (allDone) showCongratsDialog = true
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = bgColor
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 14.dp)
        ) {
            // ---- Top bar ----
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Leading: actual back navigation
                IconButton(onClick = onBack) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        //Icons.Default.Settings,
                        contentDescription = stringResource(R.string.action_back_to_settings),
                        tint = onBgColor
                    )
                }

                Text(
                    text = project?.name ?: stringResource(R.string.title_execution),
                    style = MaterialTheme.typography.titleMedium,
                    color = onBgColor,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center
                )

                // Trailing: utility icons + settings gear
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    // Settings gear → go to ProjectSettingsScreen for THIS project
                    IconButton(onClick = { onOpenSettings(projectId) }) {
                        Icon(
                            Icons.Default.Settings,
                            contentDescription = stringResource(R.string.action_project_settings),
                            tint = onBgColor
                        )
                    }

                    // Theme quick toggle
                    val themeIcon = when (executionTheme) {
                        ExecutionTheme.DARK -> Icons.Default.DarkMode
                        ExecutionTheme.LIGHT -> Icons.Default.LightMode
                        ExecutionTheme.WARM -> Icons.Default.WbSunny
                    }
                    IconButton(onClick = {
                        executionTheme = when (executionTheme) {
                            ExecutionTheme.DARK -> ExecutionTheme.LIGHT
                            ExecutionTheme.LIGHT -> ExecutionTheme.WARM
                            ExecutionTheme.WARM -> ExecutionTheme.DARK
                        }
                    }) {
                        Icon(themeIcon, contentDescription = stringResource(R.string.toggle_theme), tint = onBgColor)
                    }

                    // Keep screen on toggle
                    IconButton(onClick = { keepScreenOn = !keepScreenOn }) {
                        Icon(
                            if (keepScreenOn) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                            contentDescription = "Keep Screen On",
                            tint = if (keepScreenOn) MaterialTheme.colorScheme.primary else onBgColor.copy(alpha = 0.5f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (project == null) {
                Text(
                    text = stringResource(R.string.execution_project_not_found),
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
                        modifier = Modifier
                            .weight(1f)
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        ExecutionProgressBar(uiState = uiState, onBgColor = onBgColor)
                        val pattern = uiState.pattern
                        if (pattern != null && pattern.availableSizes.isNotEmpty()) {
                            SizeSelector(
                                sizes = pattern.availableSizes,
                                selectedSize = uiState.selectedSize,
                                onSizeSelected = viewModel::selectSize
                            )
                        }
                        MainStepCard(uiState = uiState, onBgColor = onBgColor, theme = executionTheme) { selectedStitch = it }
                        RowCounterCard(counter = rowCounter, onBgColor = onBgColor, theme = executionTheme) { rowCounter = it }
                        NavigationControls(uiState = uiState, viewModel = viewModel, onBgColor = onBgColor)
                    }
                    Column(
                        modifier = Modifier
                            .weight(0.8f)
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        VoiceStubCard(theme = executionTheme)
                        AidiCompanion(modifier = Modifier.fillMaxWidth())
                        NoteCard(uiState, theme = executionTheme) { showNoteDialog = true }
                    }
                }
            } else {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    ExecutionProgressBar(uiState = uiState, onBgColor = onBgColor)
                    val pattern = uiState.pattern
                    if (pattern != null && pattern.availableSizes.isNotEmpty()) {
                        SizeSelector(
                            sizes = pattern.availableSizes,
                            selectedSize = uiState.selectedSize,
                            onSizeSelected = viewModel::selectSize
                        )
                    }
                    VoiceStubCard(theme = executionTheme)
                    MainStepCard(uiState = uiState, onBgColor = onBgColor, theme = executionTheme) { selectedStitch = it }
                    RowCounterCard(counter = rowCounter, onBgColor = onBgColor, theme = executionTheme) { rowCounter = it }
                    NoteCard(uiState, theme = executionTheme) { showNoteDialog = true }
                    NavigationControls(uiState = uiState, viewModel = viewModel, onBgColor = onBgColor)
                }
            }
        }
    }

    // Dialogs
    if (showNoteDialog) {
        StepNoteDialog(
            currentNote = uiState.currentStepProgress?.note.orEmpty(),
            onDismiss = { showNoteDialog = false },
            onSave = { note ->
                viewModel.saveNote(note)
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

    if (showCongratsDialog) {
        CongratsDialog(
            projectName = project?.name ?: "Your Project",
            onDismiss = { showCongratsDialog = false },
            onNavigateBack = onBack
        )
    }
}

// ---- Progress Bar ----
@Composable
private fun ExecutionProgressBar(
    uiState: ProjectExecutionUiState,
    onBgColor: Color
) {
    val animatedProgress by animateFloatAsState(
        targetValue = if (uiState.totalSteps > 0) (uiState.project?.currentStepIndex ?: 0).toFloat() / uiState.totalSteps else 0f,
        animationSpec = tween(durationMillis = 600),
        label = "execution-progress"
    )
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                "Step ${uiState.currentStepNumber} of ${uiState.totalSteps}",
                style = MaterialTheme.typography.bodyMedium,
                color = onBgColor.copy(alpha = 0.8f)
            )
            Text(
                "${uiState.project?.progressPercent ?: 0}%",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )
        }
        LinearProgressIndicator(
            progress = { animatedProgress },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp)),
            color = MaterialTheme.colorScheme.primary,
            trackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
        )
    }
}

// ---- Voice Stub Card ----
@Composable
private fun VoiceStubCard(theme: ExecutionTheme) {
    val cardBg = when (theme) {
        ExecutionTheme.DARK -> Color(0xFF252540)
        ExecutionTheme.LIGHT -> MaterialTheme.colorScheme.surfaceVariant
        ExecutionTheme.WARM -> Color(0xFF3D2510)
    }
    Card(
        colors = CardDefaults.cardColors(containerColor = cardBg),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            VoiceIconButton(
                icon = Icons.Default.Mic,
                label = "Voice Input",
                tint = MaterialTheme.colorScheme.primary,
                enabled = false
            )
            Divider(
                modifier = Modifier
                    .height(36.dp)
                    .width(1.dp),
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
            )
            VoiceIconButton(
                icon = Icons.Default.VolumeUp,
                label = "Read Aloud",
                tint = MaterialTheme.colorScheme.secondary,
                enabled = false
            )
            Text(
                text = "Voice AI — coming soon",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.End
            )
        }
    }
}

@Composable
private fun VoiceIconButton(icon: ImageVector, label: String, tint: Color, enabled: Boolean) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(4.dp)) {
        IconButton(onClick = {}, enabled = enabled, modifier = Modifier.size(48.dp)) {
            Icon(icon, contentDescription = label, tint = tint.copy(alpha = if (enabled) 1f else 0.4f), modifier = Modifier.size(28.dp))
        }
        Text(label, style = MaterialTheme.typography.labelSmall, color = tint.copy(alpha = 0.5f))
    }
}

// ---- Main Step Card ----
@Composable
private fun MainStepCard(
    uiState: ProjectExecutionUiState,
    onBgColor: Color,
    theme: ExecutionTheme,
    onStitchClick: (String) -> Unit
) {
    val cardBg = when (theme) {
        ExecutionTheme.DARK -> Color(0xFF252540)
        ExecutionTheme.LIGHT -> MaterialTheme.colorScheme.surfaceVariant
        ExecutionTheme.WARM -> Color(0xFF3D2510)
    }
    Card(
        colors = CardDefaults.cardColors(containerColor = cardBg),
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize()
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(
                "Current Instruction",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 1.sp
            )
            AnimatedContent(targetState = uiState.currentStep, label = "step-card") { step ->
                if (step != null) {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        StitchLinkText(
                            text = step.instructionData,
                            knownStitches = KNOWN_STITCHES,
                            onStitchClick = onStitchClick
                        )
                        if (step.stitchCountData != null) {
                            Surface(
                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    "Target: ${step.stitchCountData} stitches",
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                } else {
                    Text(
                        "🎉 All steps completed!",
                        style = MaterialTheme.typography.headlineSmall,
                        color = onBgColor
                    )
                }
            }
        }
    }
}

// ---- Row Counter Card ----
@Composable
private fun RowCounterCard(
    counter: Int,
    onBgColor: Color,
    theme: ExecutionTheme,
    onCounterChange: (Int) -> Unit
) {
    val cardBg = when (theme) {
        ExecutionTheme.DARK -> Color(0xFF1E1E38)
        ExecutionTheme.LIGHT -> MaterialTheme.colorScheme.surface
        ExecutionTheme.WARM -> Color(0xFF2C1A0E)
    }
    Card(
        colors = CardDefaults.cardColors(containerColor = cardBg),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Row Counter",
                    style = MaterialTheme.typography.titleSmall,
                    color = onBgColor.copy(alpha = 0.7f),
                    fontWeight = FontWeight.SemiBold
                )
                IconButton(
                    onClick = { onCounterChange(0) },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        Icons.Default.Refresh,
                        contentDescription = "Reset counter",
                        tint = onBgColor.copy(alpha = 0.4f),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // -10 button
                CounterButton(label = "-10", onClick = { onCounterChange((counter - 10).coerceAtLeast(0)) })
                // -1 button
                CounterButton(label = "-1", onClick = { onCounterChange((counter - 1).coerceAtLeast(0)) })

                // Big counter display
                Text(
                    text = "$counter",
                    style = MaterialTheme.typography.displaySmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.width(80.dp),
                    textAlign = TextAlign.Center
                )

                // +1 button
                CounterButton(label = "+1", positive = true, onClick = { onCounterChange(counter + 1) })
                // +10 button
                CounterButton(label = "+10", positive = true, onClick = { onCounterChange(counter + 10) })
            }
        }
    }
}

@Composable
private fun CounterButton(label: String, positive: Boolean = false, onClick: () -> Unit) {
    val bgColor = if (positive)
        MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
    else
        MaterialTheme.colorScheme.outline.copy(alpha = 0.12f)
    val textColor = if (positive)
        MaterialTheme.colorScheme.primary
    else
        MaterialTheme.colorScheme.onSurfaceVariant

    Surface(
        modifier = Modifier
            .size(52.dp)
            .clip(CircleShape)
            .clickable { onClick() },
        color = bgColor,
        shape = CircleShape
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = textColor
            )
        }
    }
}

// ---- Note Card ----
@Composable
private fun NoteCard(
    uiState: ProjectExecutionUiState,
    theme: ExecutionTheme,
    onEditNote: () -> Unit
) {
    val cardBg = when (theme) {
        ExecutionTheme.DARK -> Color(0xFF1E1E38)
        ExecutionTheme.LIGHT -> MaterialTheme.colorScheme.surface
        ExecutionTheme.WARM -> Color(0xFF2C1A0E)
    }
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize()
            .clickable { onEditNote() },
        colors = CardDefaults.cardColors(containerColor = cardBg),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.Top
        ) {
            Icon(Icons.Default.EditNote, contentDescription = "Notes", tint = MaterialTheme.colorScheme.secondary, modifier = Modifier.size(20.dp))
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text("My Notes", style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.secondary)
                Text(
                    text = uiState.currentStepProgress?.note ?: "Tap to add a note for this step…",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (uiState.currentStepProgress?.note == null)
                        MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                    else MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

// ---- Navigation Controls ----
@Composable
private fun NavigationControls(
    uiState: ProjectExecutionUiState,
    viewModel: ProjectExecutionViewModel,
    onBgColor: Color
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Previous
        FilledTonalIconButton(
            onClick = viewModel::previousStep,
            enabled = uiState.canGoPrevious,
            modifier = Modifier.size(52.dp)
        ) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.action_previous))
        }

        // Mark Done / Next
        Button(
            onClick = viewModel::markCurrentStepDone,
            enabled = uiState.canGoNext || (!uiState.isCurrentStepDone && uiState.totalSteps > 0),
            modifier = Modifier
                .weight(1f)
                .height(52.dp),
            shape = RoundedCornerShape(14.dp)
        ) {
            val label = when {
                uiState.isCurrentStepDone -> "Next Step →"
                uiState.totalSteps > 0 && (uiState.project?.currentStepIndex ?: 0) == uiState.totalSteps - 1 -> "Finish ✓"
                else -> "Mark Done ✓"
            }
            Text(label, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        }

        // Next
        FilledTonalIconButton(
            onClick = viewModel::nextStep,
            enabled = uiState.canGoNext,
            modifier = Modifier.size(52.dp)
        ) {
            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Next")
        }
    }
}

// ---- Step Note Dialog ----
@Composable
private fun StepNoteDialog(
    currentNote: String,
    onDismiss: () -> Unit,
    onSave: (String) -> Unit
) {
    var note by remember(currentNote) { mutableStateOf(currentNote) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.dialog_step_note_title)) },
        text = {
            OutlinedTextField(
                value = note,
                onValueChange = { note = it },
                label = { Text(stringResource(R.string.input_personal_note)) },
                minLines = 4
            )
        },
        confirmButton = {
            Button(onClick = { onSave(note) }) { Text(stringResource(R.string.action_save)) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(R.string.action_cancel)) }
        }
    )
}

// ---- Size Selector ----
@Composable
private fun SizeSelector(
    sizes: List<String>,
    selectedSize: String?,
    onSizeSelected: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = stringResource(R.string.execution_select_size),
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
                        ButtonDefaults.outlinedButtonColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    } else {
                        ButtonDefaults.outlinedButtonColors()
                    }
                ) {
                    Text(size)
                }
            }
        }
    }
}

// ---- Congratulations Dialog ----
@Composable
private fun CongratsDialog(
    projectName: String,
    onDismiss: () -> Unit,
    onNavigateBack: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Text("🎉", fontSize = 48.sp)
        },
        title = {
            Text(
                "Project Complete!",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        },
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Congratulations! You've finished \"$projectName\".",
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center
                )
                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("🪙", fontSize = 28.sp)
                        Column {
                            Text(
                                "+50 coins earned!",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Text(
                                "Reward for completing your project",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onDismiss()
                    onNavigateBack()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Share & Finish 🎊")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
}
