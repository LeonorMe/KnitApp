package com.malha.app.feature.projects

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.core.graphics.toColorInt
import androidx.lifecycle.viewmodel.compose.viewModel
import com.malha.app.R
import com.malha.app.domain.model.Pattern
import com.malha.app.domain.model.ProjectStatus

private val PROJECT_PALETTE = listOf(
    "#FF8A65", // Terracotta
    "#FFB74D", // Orange/Gold
    "#FFD54F", // Amber
    "#AED581", // Light Green
    "#4DB6AC", // Teal
    "#4FC3F7", // Sky Blue
    "#7986CB", // Indigo
    "#BA68C8", // Purple
    "#F06292", // Pink
    "#A1887F", // Warm Brown
    "#90A4AE", // Blue-Grey
    "#3F6B57"  // Sage (brand colour)
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectSettingsScreen(
    projectId: String,
    onStartProject: (String) -> Unit,
    onBack: () -> Unit,
    viewModel: ProjectSettingsViewModel = viewModel(
        factory = ProjectSettingsViewModel.factory(projectId)
    )
) {
    val uiState by viewModel.uiState.collectAsState()
    var showPatternDialog by remember { mutableStateOf(false) }
    var showDuplicateDialog by remember { mutableStateOf(false) }
    var showBuyYarnDialog by remember { mutableStateOf(false) }
    var showBuyNeedleDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.configure_project)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = stringResource(R.string.go_back)
                        )
                    }
                },
                actions = {
                    // Duplicate Project Action
                    IconButton(onClick = { showDuplicateDialog = true }) {
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = stringResource(R.string.duplicate_project)
                        )
                    }

                    // Coins Counter
                    Row(
                        modifier = Modifier.padding(end = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_coin),
                            contentDescription = stringResource(R.string.coins),
                            tint = Color.Unspecified
                        )
                        Text(
                            text = uiState.coins.toString(),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { padding ->
        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 16.dp, vertical = 12.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Active/Finished Project Locked Banner
                if (uiState.isReadOnlyConfig) {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                            Text(
                                text = stringResource(R.string.status_locked_banner),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        }
                    }
                }

                // 1. Project Name (Editable in all states)
                OutlinedTextField(
                    value = uiState.name,
                    onValueChange = viewModel::updateName,
                    label = { Text(stringResource(R.string.project_name_label)) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                // 2. Compact Project Color Selector (Less visually dominant)
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = stringResource(R.string.project_color),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        PROJECT_PALETTE.forEach { hex ->
                            val swatchColor = remember(hex) {
                                runCatching { Color(hex.toColorInt()) }.getOrElse { Color.Gray }
                            }
                            val isSelected = hex.equals(uiState.colorHex, ignoreCase = true)
                            Surface(
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(CircleShape)
                                    .clickable { viewModel.updateColor(hex) },
                                shape = CircleShape,
                                color = swatchColor,
                                border = BorderStroke(
                                    width = if (isSelected) 2.5.dp else 1.dp,
                                    color = if (isSelected) MaterialTheme.colorScheme.primary else Color.LightGray.copy(alpha = 0.5f)
                                )
                            ) {
                                if (isSelected) {
                                    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = stringResource(R.string.selected),
                                            tint = if (swatchColor.luminance() < 0.35f) Color.White else Color.Black,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                HorizontalDivider()

                // 3. Pattern Selection Workflow
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(R.string.pattern_selection_title),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        if (!uiState.isReadOnlyConfig && uiState.selectedPattern != null) {
                            TextButton(onClick = { showPatternDialog = true }) {
                                Text(stringResource(R.string.change_pattern))
                            }
                        }
                    }

                    if (uiState.selectedPattern == null) {
                        OutlinedCard(
                            onClick = { if (!uiState.isReadOnlyConfig) showPatternDialog = true },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary
                                )
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = stringResource(R.string.select_pattern_prompt),
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Text(
                                        text = stringResource(R.string.missing_pattern_requirement),
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.error
                                    )
                                }
                                Icon(
                                    imageVector = Icons.Default.ChevronRight,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    } else {
                        val pattern = uiState.selectedPattern!!
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Description,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(32.dp)
                                )
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = pattern.title,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                    if (!pattern.designer.isNullOrBlank()) {
                                        Text(
                                            text = pattern.designer,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                    Text(
                                        text = "${pattern.craft.name.lowercase().replaceFirstChar { it.uppercase() }} • ${pattern.difficulty.name.lowercase().replaceFirstChar { it.uppercase() }}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        }
                    }
                }

                // 4. Size Selection Workflow
                if (uiState.selectedPattern != null && uiState.availableSizes.isNotEmpty()) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = stringResource(R.string.project_size),
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            uiState.availableSizes.forEach { size ->
                                val isSelected = uiState.selectedSize.equals(size, ignoreCase = true)
                                FilterChip(
                                    selected = isSelected,
                                    onClick = {
                                        if (!uiState.isReadOnlyConfig) {
                                            viewModel.updateSize(size)
                                        }
                                    },
                                    label = { Text(size) },
                                    enabled = !uiState.isReadOnlyConfig || isSelected
                                )
                            }
                        }
                    }
                }

                // 5. Pattern Requirements Display (Read-only from pattern)
                if (uiState.patternRequirement != null) {
                    val req = uiState.patternRequirement!!
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        ),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = stringResource(R.string.pattern_requirements_title),
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "• ${stringResource(R.string.required_needles)}: ${req.recommendedNeedleType} ${req.recommendedNeedleMm}mm",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                text = "• ${stringResource(R.string.required_yarn)}: ~${req.baseYardageMeters}m (${req.recommendedYarnWeight})",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }

                HorizontalDivider()

                // 6. Inventory Integration - Yarn Selection
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = stringResource(R.string.my_inventory_yarn),
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )

                    var yarnExpanded by remember { mutableStateOf(false) }
                    ExposedDropdownMenuBox(
                        expanded = yarnExpanded && !uiState.isReadOnlyConfig,
                        onExpandedChange = { if (!uiState.isReadOnlyConfig) yarnExpanded = it }
                    ) {
                        val selectedYarn = uiState.selectedYarn
                        OutlinedTextField(
                            value = selectedYarn?.let {
                                "${it.name} (${it.weightCategory ?: stringResource(R.string.no_yarn_size)})"
                            } ?: stringResource(R.string.no_yarn_in_inventory),
                            onValueChange = {},
                            readOnly = true,
                            enabled = !uiState.isReadOnlyConfig,
                            trailingIcon = {
                                if (!uiState.isReadOnlyConfig) {
                                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = yarnExpanded)
                                }
                            },
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth()
                        )
                        if (uiState.yarns.isNotEmpty()) {
                            ExposedDropdownMenu(
                                expanded = yarnExpanded,
                                onDismissRequest = { yarnExpanded = false }
                            ) {
                                uiState.yarns.forEach { yarn ->
                                    val isRecommended = uiState.recommendedYarn?.id == yarn.id
                                    DropdownMenuItem(
                                        text = {
                                            Text(
                                                text = "${yarn.name} (${yarn.weightCategory ?: stringResource(R.string.no_yarn_size)})${if (isRecommended) " ⭐ ${stringResource(R.string.recommended_badge)}" else ""}"
                                            )
                                        },
                                        onClick = {
                                            viewModel.selectYarn(yarn.id)
                                            yarnExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    if (uiState.recommendedYarn == null && !uiState.isReadOnlyConfig) {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.ShoppingCart, contentDescription = stringResource(R.string.store))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = stringResource(R.string.recomended_yarn_missing),
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onErrorContainer
                                    )
                                }
                                Button(
                                    onClick = { showBuyYarnDialog = true },
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                                ) {
                                    Text(stringResource(R.string.buy))
                                }
                            }
                        }
                    }
                }

                // 7. Inventory Integration - Needle Selection
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = stringResource(R.string.my_inventory_needles),
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )

                    var needleExpanded by remember { mutableStateOf(false) }
                    ExposedDropdownMenuBox(
                        expanded = needleExpanded && !uiState.isReadOnlyConfig,
                        onExpandedChange = { if (!uiState.isReadOnlyConfig) needleExpanded = it }
                    ) {
                        val selectedNeedle = uiState.selectedNeedle
                        OutlinedTextField(
                            value = selectedNeedle?.let { "${it.name} (${it.sizeMm ?: 0.0}mm)" }
                                ?: stringResource(R.string.no_needles_in_inventory),
                            onValueChange = {},
                            readOnly = true,
                            enabled = !uiState.isReadOnlyConfig,
                            trailingIcon = {
                                if (!uiState.isReadOnlyConfig) {
                                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = needleExpanded)
                                }
                            },
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth()
                        )
                        if (uiState.needles.isNotEmpty()) {
                            ExposedDropdownMenu(
                                expanded = needleExpanded,
                                onDismissRequest = { needleExpanded = false }
                            ) {
                                uiState.needles.forEach { needle ->
                                    val isRecommended = uiState.recommendedNeedle?.id == needle.id
                                    DropdownMenuItem(
                                        text = {
                                            Text(
                                                text = "${needle.name} (${needle.sizeMm}mm)${if (isRecommended) " ⭐ ${stringResource(R.string.recommended_badge)}" else ""}"
                                            )
                                        },
                                        onClick = {
                                            viewModel.selectNeedle(needle.id)
                                            needleExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    if (uiState.recommendedNeedle == null && !uiState.isReadOnlyConfig) {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.ShoppingCart, contentDescription = stringResource(R.string.store))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = stringResource(R.string.necessarie_needles),
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onErrorContainer
                                    )
                                }
                                Button(
                                    onClick = { showBuyNeedleDialog = true },
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                                ) {
                                    Text(stringResource(R.string.buy))
                                }
                            }
                        }
                    }
                }

                // 8. Adaptation Engine v1 Card
                if (uiState.adaptationResult != null) {
                    val adaptation = uiState.adaptationResult!!
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = if (adaptation.isMismatch) {
                                MaterialTheme.colorScheme.tertiaryContainer
                            } else {
                                MaterialTheme.colorScheme.secondaryContainer
                            }
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = if (adaptation.isMismatch) Icons.Default.Tune else Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = if (adaptation.isMismatch) MaterialTheme.colorScheme.onTertiaryContainer else MaterialTheme.colorScheme.onSecondaryContainer
                                )
                                Text(
                                    text = stringResource(R.string.adaptation_engine_title),
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = if (adaptation.isMismatch) MaterialTheme.colorScheme.onTertiaryContainer else MaterialTheme.colorScheme.onSecondaryContainer
                                )
                            }
                            if (adaptation.summary != null) {
                                Text(
                                    text = adaptation.summary,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = if (adaptation.isMismatch) MaterialTheme.colorScheme.onTertiaryContainer else MaterialTheme.colorScheme.onSecondaryContainer
                                )
                            } else {
                                Text(
                                    text = stringResource(R.string.adaptation_matches_perfectly),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer
                                )
                            }
                        }
                    }
                }

                // Error message
                if (uiState.error != null) {
                    Text(
                        text = uiState.error.orEmpty(),
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // 9. Main Action Button: Start / Continue / Finished
                val isFinished = uiState.projectStatus == ProjectStatus.FINISHED
                val isReadOnly = uiState.isReadOnlyConfig
                val isReady = uiState.isReadyToStart

                val buttonText = when {
                    isFinished -> stringResource(R.string.project_finished)
                    isReadOnly -> stringResource(R.string.continue_project)
                    else -> stringResource(R.string.start_project)
                }

                val buttonEnabled = when {
                    isFinished -> false
                    isReadOnly -> true
                    else -> isReady
                }

                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Button(
                        onClick = {
                            viewModel.startOrContinueProject { pid ->
                                onStartProject(pid)
                            }
                        },
                        enabled = buttonEnabled,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                    ) {
                        Text(buttonText, style = MaterialTheme.typography.titleMedium)
                    }

                    // Show reason if disabled in Planning / Draft state
                    if (!isReadOnly && !isFinished && !isReady) {
                        val reasons = uiState.missingRequirements.joinToString(", ")
                        Text(
                            text = stringResource(R.string.reason_to_start_prefix, reasons),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        }
    }

    // Pattern Selection Search Dialog
    if (showPatternDialog) {
        PatternSearchDialog(
            patterns = uiState.filteredPatterns,
            searchQuery = uiState.patternSearchQuery,
            onSearchQueryChange = viewModel::updatePatternSearchQuery,
            onPatternSelected = { pattern ->
                viewModel.selectPattern(pattern.id)
                showPatternDialog = false
            },
            onDismiss = { showPatternDialog = false }
        )
    }

    // Duplicate Project Dialog
    if (showDuplicateDialog) {
        var copyName by remember { mutableStateOf("${uiState.name} (Copy)") }
        AlertDialog(
            onDismissRequest = { showDuplicateDialog = false },
            title = { Text(stringResource(R.string.duplicate_project_dialog_title)) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(stringResource(R.string.duplicate_project_dialog_message))
                    OutlinedTextField(
                        value = copyName,
                        onValueChange = { copyName = it },
                        label = { Text(stringResource(R.string.duplicate_project_name_label)) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.duplicateProject(copyName) { newId ->
                            showDuplicateDialog = false
                            onStartProject(newId)
                        }
                    }
                ) {
                    Text(stringResource(R.string.duplicate_action))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDuplicateDialog = false }) {
                    Text(stringResource(R.string.cancel_action))
                }
            }
        )
    }

    // Purchase Yarn Confirmation
    if (showBuyYarnDialog) {
        AlertDialog(
            onDismissRequest = { showBuyYarnDialog = false },
            title = { Text(stringResource(R.string.buy_yarn_dialog_title)) },
            text = { Text(stringResource(R.string.buy_yarn_dialog_text)) },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.buyYarnFromStore()
                        showBuyYarnDialog = false
                    }
                ) {
                    Text(stringResource(R.string.confirm_purchase))
                }
            },
            dismissButton = {
                TextButton(onClick = { showBuyYarnDialog = false }) {
                    Text(stringResource(R.string.cancel_action))
                }
            }
        )
    }

    // Purchase Needle Confirmation
    if (showBuyNeedleDialog) {
        AlertDialog(
            onDismissRequest = { showBuyNeedleDialog = false },
            title = { Text(stringResource(R.string.buy_needle_dialog_title)) },
            text = { Text(stringResource(R.string.buy_needle_dialog_text)) },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.buyNeedlesFromStore()
                        showBuyNeedleDialog = false
                    }
                ) {
                    Text(stringResource(R.string.confirm_purchase))
                }
            },
            dismissButton = {
                TextButton(onClick = { showBuyNeedleDialog = false }) {
                    Text(stringResource(R.string.cancel_action))
                }
            }
        )
    }
}

@Composable
private fun PatternSearchDialog(
    patterns: List<Pattern>,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onPatternSelected: (Pattern) -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.85f)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.pattern_selection_title),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = stringResource(R.string.close))
                    }
                }

                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = onSearchQueryChange,
                    placeholder = { Text(stringResource(R.string.search_patterns_placeholder)) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                if (patterns.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = stringResource(R.string.no_patterns_matching_search),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(patterns) { pattern ->
                            Card(
                                onClick = { onPatternSelected(pattern) },
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                                ),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Description,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(28.dp)
                                    )
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = pattern.title,
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold
                                        )
                                        if (!pattern.designer.isNullOrBlank()) {
                                            Text(
                                                text = pattern.designer,
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                        if (pattern.availableSizes.isNotEmpty()) {
                                            Text(
                                                text = "${stringResource(R.string.all_sizes)}: ${pattern.availableSizes.joinToString(", ")}",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = MaterialTheme.colorScheme.primary
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
