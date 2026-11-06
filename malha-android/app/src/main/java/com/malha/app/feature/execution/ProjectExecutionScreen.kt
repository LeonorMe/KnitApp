package com.malha.app.feature.execution

import android.app.Activity
import android.view.WindowManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.malha.app.R
import com.malha.app.core.design.component.AidiCompanion
import com.malha.app.core.design.component.ImagePlaceholder
import com.malha.app.core.design.component.KNOWN_STITCHES
import com.malha.app.core.design.component.StitchLinkText
import com.malha.app.core.design.component.TechniqueHelpBottomSheet
import com.malha.app.core.util.ImageStorageHelper
import com.malha.app.domain.pattern.CountUnit
import com.malha.app.domain.pattern.InstructionNodeType

// ------ Theme enum for quick toggle ------
enum class ExecutionTheme { DARK, LIGHT, WARM }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectExecutionScreen(
    projectId: String,
    onBack: () -> Unit,
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
    var showRoadmapSheet by remember { mutableStateOf(false) }

    // Quick theme toggle
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

    // Target count and whether counter reaches it
    val targetStitchCount = uiState.targetCount
    val isTargetReached = targetStitchCount != null && targetStitchCount > 0 && uiState.currentCount >= targetStitchCount

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = bgColor
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 10.dp)
        ) {
            // ---- Top bar ----
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Leading: back navigation
                IconButton(
                    onClick = onBack,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.action_back),
                        tint = onBgColor,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(6.dp))

                // Project name & size subtitle
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = project?.name ?: stringResource(R.string.title_execution),
                        style = MaterialTheme.typography.titleMedium,
                        color = onBgColor,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                    )
                    if (!project?.selectedSize.isNullOrBlank()) {
                        Text(
                            text = stringResource(R.string.execution_size_label, project?.selectedSize.orEmpty()),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Medium,
                            maxLines = 1
                        )
                    }
                }

                Spacer(modifier = Modifier.width(4.dp))

                // Quick theme toggle
                val themeIcon = when (executionTheme) {
                    ExecutionTheme.DARK -> Icons.Default.DarkMode
                    ExecutionTheme.LIGHT -> Icons.Default.LightMode
                    ExecutionTheme.WARM -> Icons.Default.WbSunny
                }
                IconButton(
                    onClick = {
                        executionTheme = when (executionTheme) {
                            ExecutionTheme.DARK -> ExecutionTheme.LIGHT
                            ExecutionTheme.LIGHT -> ExecutionTheme.WARM
                            ExecutionTheme.WARM -> ExecutionTheme.DARK
                        }
                    },
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        themeIcon,
                        contentDescription = stringResource(R.string.toggle_theme),
                        tint = onBgColor,
                        modifier = Modifier.size(20.dp)
                    )
                }

                // Keep screen on
                IconButton(
                    onClick = { keepScreenOn = !keepScreenOn },
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        if (keepScreenOn) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        contentDescription = stringResource(R.string.execution_keep_screen_on),
                        tint = if (keepScreenOn) MaterialTheme.colorScheme.primary else onBgColor.copy(alpha = 0.5f),
                        modifier = Modifier.size(20.dp)
                    )
                }

                // Pattern Roadmap List
                IconButton(
                    onClick = { showRoadmapSheet = true },
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        Icons.Default.FormatListNumbered,
                        contentDescription = stringResource(R.string.execution_step_history),
                        tint = onBgColor,
                        modifier = Modifier.size(20.dp)
                    )
                }

                // Settings
                IconButton(
                    onClick = { onOpenSettings(projectId) },
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        Icons.Default.Settings,
                        contentDescription = stringResource(R.string.action_project_settings),
                        tint = onBgColor,
                        modifier = Modifier.size(20.dp)
                    )
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
                        MainStepCard(
                            uiState = uiState,
                            onBgColor = onBgColor,
                            theme = executionTheme,
                            isTargetReached = isTargetReached,
                            onStitchClick = { selectedStitch = it }
                        )
                        InstructionCounterCard(
                            uiState = uiState,
                            onBgColor = onBgColor,
                            theme = executionTheme,
                            onIncrement = { viewModel.incrementCounter() },
                            onDecrement = { viewModel.decrementCounter() },
                            onSetIncrement = { viewModel.setIncrement(it) }
                        )
                        NavigationControls(
                            uiState = uiState,
                            viewModel = viewModel,
                            onBgColor = onBgColor,
                            isTargetReached = isTargetReached,
                            onFinishClick = { showCongratsDialog = true }
                        )
                    }
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
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
                    MainStepCard(
                        uiState = uiState,
                        onBgColor = onBgColor,
                        theme = executionTheme,
                        isTargetReached = isTargetReached,
                        onStitchClick = { selectedStitch = it }
                    )
                    InstructionCounterCard(
                        uiState = uiState,
                        onBgColor = onBgColor,
                        theme = executionTheme,
                        onIncrement = { viewModel.incrementCounter() },
                        onDecrement = { viewModel.decrementCounter() },
                        onSetIncrement = { viewModel.setIncrement(it) }
                    )
                    NoteCard(uiState, theme = executionTheme) { showNoteDialog = true }
                    NavigationControls(
                        uiState = uiState,
                        viewModel = viewModel,
                        onBgColor = onBgColor,
                        isTargetReached = isTargetReached,
                        onFinishClick = { showCongratsDialog = true }
                    )
                }
            }
        }
    }

    // Dialogs & Sheets
    if (showNoteDialog) {
        StepNoteDialog(
            currentNote = uiState.currentNote.orEmpty(),
            onDismiss = { showNoteDialog = false },
            onSave = { note ->
                viewModel.saveNote(note)
                showNoteDialog = false
            }
        )
    }

    if (selectedStitch != null) {
        TechniqueHelpBottomSheet(
            techniqueId = selectedStitch!!,
            onDismiss = { selectedStitch = null }
        )
    }

    if (showRoadmapSheet && uiState.instructions.isNotEmpty()) {
        ModalBottomSheet(
            onDismissRequest = { showRoadmapSheet = false }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp)
            ) {
                Text(
                    text = stringResource(R.string.execution_step_history),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 450.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    itemsIndexed(uiState.instructions) { index, inst ->
                        val isDone = uiState.progressList.any { it.instructionSnapshotId == inst.id && it.isCompleted }
                        val isCurrent = (uiState.project?.currentStepIndex ?: 0) == index

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = when {
                                isCurrent -> MaterialTheme.colorScheme.primaryContainer
                                isDone -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                                else -> MaterialTheme.colorScheme.surface
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    viewModel.jumpToStep(index)
                                    showRoadmapSheet = false
                                }
                        ) {
                            Row(
                                modifier = Modifier
                                    .padding(14.dp)
                                    .fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Text(
                                    text = "${index + 1}",
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isCurrent) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
                                )

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = inst.displayText,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isCurrent) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = inst.sectionTitle,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.outline
                                    )
                                }

                                if (isDone) {
                                    Icon(
                                        Icons.Default.CheckCircle,
                                        contentDescription = "Completed",
                                        tint = Color(0xFF4CAF50),
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }

    if (showCongratsDialog) {
        CongratsDialog(
            projectName = project?.name ?: "Your Project",
            onDismiss = { showCongratsDialog = false },
            onConfirmFinish = { photoUri ->
                viewModel.finishProject(photoUri = photoUri, publishToFeed = true)
                showCongratsDialog = false
                onBack()
            }
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
        targetValue = uiState.progressFloat,
        animationSpec = tween(durationMillis = 600),
        label = "execution-progress"
    )
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                stringResource(R.string.execution_step_of, uiState.currentStepNumber, uiState.totalSteps),
                style = MaterialTheme.typography.bodyMedium,
                color = onBgColor.copy(alpha = 0.9f),
                fontWeight = FontWeight.Medium
            )
            Text(
                stringResource(R.string.execution_progress, uiState.progressPercent),
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

// ---- Main Step Card ----
@Composable
private fun MainStepCard(
    uiState: ProjectExecutionUiState,
    onBgColor: Color,
    theme: ExecutionTheme,
    isTargetReached: Boolean = false,
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
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    stringResource(R.string.execution_current_instruction),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 1.sp
                )

                val sectionTitle = uiState.currentInstruction?.sectionTitle
                if (!sectionTitle.isNullOrBlank()) {
                    Text(
                        text = sectionTitle,
                        style = MaterialTheme.typography.labelSmall,
                        color = onBgColor.copy(alpha = 0.6f)
                    )
                }
            }

            AnimatedContent(
                targetState = uiState.currentInstruction?.id ?: uiState.currentStep?.id,
                label = "step-card"
            ) { _ ->
                val instructionText = uiState.currentInstruction?.displayText
                    ?: uiState.currentStep?.instructionData.orEmpty()

                if (instructionText.isNotBlank()) {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        StitchLinkText(
                            text = instructionText,
                            knownStitches = KNOWN_STITCHES,
                            onStitchClick = onStitchClick,
                            textColor = onBgColor
                        )

                        val targetCount = uiState.targetCount
                        if (targetCount != null && targetCount > 0) {
                            val targetBgColor = if (isTargetReached) {
                                Color(0xFF2E7D32).copy(alpha = 0.25f)
                            } else {
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                            }
                            val targetTextColor = if (isTargetReached) {
                                Color(0xFF4CAF50)
                            } else {
                                MaterialTheme.colorScheme.primary
                            }
                            Surface(
                                color = targetBgColor,
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = if (isTargetReached) {
                                        "✓ " + stringResource(R.string.execution_target_stitches, targetCount.toString())
                                    } else {
                                        stringResource(R.string.execution_target_stitches, targetCount.toString())
                                    },
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = targetTextColor,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                } else {
                    Text(
                        stringResource(R.string.execution_all_steps_done),
                        style = MaterialTheme.typography.headlineSmall,
                        color = onBgColor
                    )
                }
            }
        }
    }
}

// ---- Instruction Counter Card with Divisor Increments ----
@Composable
private fun InstructionCounterCard(
    uiState: ProjectExecutionUiState,
    onBgColor: Color,
    theme: ExecutionTheme,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit,
    onSetIncrement: (Int) -> Unit
) {
    val cardBg = when (theme) {
        ExecutionTheme.DARK -> Color(0xFF1E1E38)
        ExecutionTheme.LIGHT -> MaterialTheme.colorScheme.surface
        ExecutionTheme.WARM -> Color(0xFF2C1A0E)
    }

    val isCounted = uiState.currentInstruction == null ||
            uiState.currentInstruction?.type == InstructionNodeType.COUNTED_ACTION
    val target = uiState.targetCount

    Card(
        colors = CardDefaults.cardColors(containerColor = cardBg),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    stringResource(R.string.execution_counter_label),
                    style = MaterialTheme.typography.titleSmall,
                    color = onBgColor.copy(alpha = 0.8f),
                    fontWeight = FontWeight.SemiBold
                )

                if (isCounted && uiState.suggestedIncrements.size > 1) {
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        uiState.suggestedIncrements.forEach { inc ->
                            val isSelected = uiState.selectedIncrement == inc
                            FilterChip(
                                selected = isSelected,
                                onClick = { onSetIncrement(inc) },
                                label = { Text("+$inc") }
                            )
                        }
                    }
                }
            }

            if (isCounted) {
                // Large counter and primary action button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Decrement
                    Surface(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                            .clickable { onDecrement() },
                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f),
                        shape = CircleShape
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                Icons.Default.Remove,
                                contentDescription = "Decrement",
                                tint = onBgColor
                            )
                        }
                    }

                    // Large Count Display
                    Row(
                        verticalAlignment = Alignment.Bottom,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "${uiState.currentCount}",
                            style = MaterialTheme.typography.displayMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = if (target != null && uiState.currentCount >= target) Color(0xFF4CAF50) else MaterialTheme.colorScheme.primary
                        )
                        if (target != null) {
                            Text(
                                text = " / $target",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = onBgColor.copy(alpha = 0.6f),
                                modifier = Modifier.padding(bottom = 6.dp, start = 4.dp)
                            )
                        }
                    }

                    // Primary Increment Button
                    Surface(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .clickable { onIncrement() },
                        color = MaterialTheme.colorScheme.primary,
                        shape = CircleShape
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = "+${uiState.selectedIncrement}",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    }
                }
            } else if (uiState.currentInstruction?.type == InstructionNodeType.MEASUREMENT_CHECK) {
                Text(
                    text = "Target: ${uiState.currentInstruction?.measurementValue} ${uiState.currentInstruction?.measurementUnit}",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            } else {
                Text(
                    text = uiState.currentInstruction?.displayText ?: "Non-counted action",
                    style = MaterialTheme.typography.bodyLarge,
                    color = onBgColor
                )
            }
        }
    }
}

// ---- Expandable Note Card ----
@Composable
private fun NoteCard(
    uiState: ProjectExecutionUiState,
    theme: ExecutionTheme,
    onEditNote: () -> Unit
) {
    var isExpanded by remember { mutableStateOf(false) }
    val cardBg = when (theme) {
        ExecutionTheme.DARK -> Color(0xFF1E1E38)
        ExecutionTheme.LIGHT -> MaterialTheme.colorScheme.surface
        ExecutionTheme.WARM -> Color(0xFF2C1A0E)
    }
    val noteTextColor = when (theme) {
        ExecutionTheme.DARK -> Color(0xFFE8E8F0)
        ExecutionTheme.LIGHT -> MaterialTheme.colorScheme.onSurface
        ExecutionTheme.WARM -> Color(0xFFF5E6D3)
    }
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(),
        colors = CardDefaults.cardColors(containerColor = cardBg),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { isExpanded = !isExpanded },
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.EditNote,
                        contentDescription = stringResource(R.string.execution_my_notes),
                        tint = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.size(22.dp)
                    )
                    Text(
                        stringResource(R.string.execution_my_notes),
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.secondary,
                        fontWeight = FontWeight.Bold
                    )
                }
                IconButton(
                    onClick = { isExpanded = !isExpanded },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = stringResource(R.string.execution_my_notes),
                        tint = noteTextColor.copy(alpha = 0.7f)
                    )
                }
            }

            val currentNote = uiState.currentNote
            if (isExpanded) {
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = currentNote ?: stringResource(R.string.execution_no_notes),
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (currentNote == null)
                        noteTextColor.copy(alpha = 0.6f)
                    else noteTextColor
                )
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedButton(
                    onClick = onEditNote,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(if (currentNote.isNullOrBlank()) stringResource(R.string.execution_add_note) else stringResource(R.string.execution_edit_note))
                }
            } else if (!currentNote.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = currentNote,
                    style = MaterialTheme.typography.bodySmall,
                    color = noteTextColor.copy(alpha = 0.85f),
                    maxLines = 1
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
    onBgColor: Color,
    isTargetReached: Boolean = false,
    onFinishClick: () -> Unit = {}
) {
    val isLastStep = uiState.totalSteps > 0 && (uiState.project?.currentStepIndex ?: 0) >= uiState.totalSteps - 1

    // Wave / pulse animation when target stitches is reached
    val infiniteTransition = rememberInfiniteTransition(label = "waveTransition")
    val waveScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.06f,
        animationSpec = infiniteRepeatable(
            animation = tween(650, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "waveScale"
    )
    val waveRotation by infiniteTransition.animateFloat(
        initialValue = -1.5f,
        targetValue = 1.5f,
        animationSpec = infiniteRepeatable(
            animation = tween(350, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "waveRotation"
    )

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Undo previous step
        FilledTonalIconButton(
            onClick = viewModel::undoPreviousStep,
            enabled = uiState.canGoPrevious,
            modifier = Modifier.size(52.dp)
        ) {
            Icon(Icons.AutoMirrored.Filled.Undo, contentDescription = stringResource(R.string.execution_undo_step))
        }

        // Mark Done / Next / Finish
        val buttonColors = when {
            isTargetReached -> ButtonDefaults.buttonColors(
                containerColor = Color(0xFF2E7D32),
                contentColor = Color.White
            )
            isLastStep -> ButtonDefaults.buttonColors(
                containerColor = Color(0xFFE65100),
                contentColor = Color.White
            )
            else -> ButtonDefaults.buttonColors()
        }

        val buttonModifier = if (isTargetReached && !uiState.isCurrentStepDone) {
            Modifier
                .weight(1f)
                .height(52.dp)
                .graphicsLayer {
                    scaleX = waveScale
                    scaleY = waveScale
                    rotationZ = waveRotation
                }
        } else {
            Modifier
                .weight(1f)
                .height(52.dp)
        }

        Button(
            onClick = {
                if (isLastStep) {
                    onFinishClick()
                } else {
                    viewModel.markCurrentStepDone()
                }
            },
            enabled = uiState.canGoNext || isLastStep || (!uiState.isCurrentStepDone && uiState.totalSteps > 0),
            colors = buttonColors,
            modifier = buttonModifier,
            shape = RoundedCornerShape(14.dp)
        ) {
            val label = when {
                uiState.isCurrentStepDone -> stringResource(R.string.execution_next_step_arrow)
                isLastStep -> stringResource(R.string.execution_finish_check)
                isTargetReached -> "✓ " + stringResource(R.string.execution_mark_done_check)
                else -> stringResource(R.string.execution_mark_done_check)
            }
            Text(label, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        }

        // Next
        FilledTonalIconButton(
            onClick = viewModel::nextStep,
            enabled = uiState.canGoNext,
            modifier = Modifier.size(52.dp)
        ) {
            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = stringResource(R.string.action_next))
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

// ---- Congratulations & Finished Photo Dialog ----
@Composable
private fun CongratsDialog(
    projectName: String,
    onDismiss: () -> Unit,
    onConfirmFinish: (String?) -> Unit
) {
    var selectedPhotoUri by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current

    val photoPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            if (uri != null) {
                selectedPhotoUri = ImageStorageHelper.saveImageToInternalStorage(context, uri) ?: uri.toString()
            }
        }
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Text("🎉", fontSize = 48.sp)
        },
        title = {
            Text(
                stringResource(R.string.congrats_dialog_title),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        },
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = stringResource(R.string.congrats_dialog_message, projectName),
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center
                )
                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("🪙", fontSize = 28.sp)
                        Column {
                            Text(
                                stringResource(R.string.congrats_coins_earned),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Text(
                                stringResource(R.string.congrats_coins_description),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                            )
                        }
                    }
                }

                // Photo attachment section
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            photoPicker.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        },
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (selectedPhotoUri != null) {
                            ImagePlaceholder(
                                label = "P",
                                imageUri = selectedPhotoUri,
                                size = 48.dp,
                                modifier = Modifier.clip(RoundedCornerShape(8.dp))
                            )
                        } else {
                            Icon(
                                Icons.Default.AddAPhoto,
                                contentDescription = stringResource(R.string.congrats_photo_prompt),
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (selectedPhotoUri != null) stringResource(R.string.congrats_photo_added) else stringResource(R.string.congrats_photo_prompt),
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = stringResource(R.string.congrats_showcase_subtext),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onConfirmFinish(selectedPhotoUri)
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.congrats_share_and_finish))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.congrats_close))
            }
        }
    )
}
