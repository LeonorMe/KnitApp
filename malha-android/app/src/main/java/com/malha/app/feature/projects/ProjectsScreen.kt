package com.malha.app.feature.projects

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableIntStateOf
import com.malha.app.R
import com.malha.app.core.design.component.ImagePlaceholder
import com.malha.app.core.share.MalhaShare
import androidx.core.graphics.toColorInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectsScreen(
    onOpenProjectExecution: (String) -> Unit,
    onOpenProjectSettings: (String) -> Unit,
    viewModel: ProjectsViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    var showCreateDialog by remember { mutableStateOf(false) }

    // NEW: Snackbar host for feedback
    val snackbarHostState = remember { SnackbarHostState() }
    val createdMessage = stringResource(R.string.project_created) // add to strings.xml

    val tabs = listOf(
        stringResource(R.string.project_on_going),
        stringResource(R.string.project_planned),
        stringResource(R.string.project_concluded)
    )
    var selectedTabIndex by remember { mutableIntStateOf(0) }

    // NEW: react to creation — navigate to settings, show snackbar, consume event
    LaunchedEffect(uiState.createdProjectId) {
        val projectId = uiState.createdProjectId ?: return@LaunchedEffect
        // Optional feedback (won't be seen if navigation happens immediately,
        // but useful if you decide to stay on the list)
        snackbarHostState.showSnackbar(createdMessage)
        onOpenProjectSettings(projectId)
        viewModel.consumeCreatedProjectId()
    }

    // NEW: surface errors via snackbar too
    LaunchedEffect(uiState.errorMessage) {
        val msg = uiState.errorMessage ?: return@LaunchedEffect
        snackbarHostState.showSnackbar(msg)
        viewModel.clearError()
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }, // NEW
        topBar = {
            Column {
                TopAppBar(
                    title = { Text(stringResource(R.string.os_meus_projetos)) },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = colorScheme.background
                    )
                )
                TabRow(selectedTabIndex = selectedTabIndex) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTabIndex == index,
                            onClick = { selectedTabIndex = index },
                            text = { Text(title) }
                        )
                    }
                }
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                viewModel.clearError()
                showCreateDialog = true
                },
                containerColor = colorScheme.onPrimaryContainer
            ) {
                Icon(
                    Icons.Default.Add,
                    tint = colorScheme.primaryContainer,
                    contentDescription = stringResource(R.string.new_project),
                    modifier = Modifier.size(32.dp)
                )
            }
        }
    ) { padding ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            color = colorScheme.background
        ) {
            val filteredProjects = when (selectedTabIndex) {
                0 -> uiState.projects.filter {
                    val isConcluded = it.projectStatus == com.malha.app.domain.model.ProjectStatus.FINISHED || it.progressPercent >= 100
                    !isConcluded && it.progressPercent > 0
                } // Ongoing: active with progress > 0
                1 -> uiState.projects.filter {
                    val isConcluded = it.projectStatus == com.malha.app.domain.model.ProjectStatus.FINISHED || it.progressPercent >= 100
                    !isConcluded && it.progressPercent == 0
                } // Planned: in planning (0% progress, step 1 not completed yet)
                else -> uiState.projects.filter {
                    it.projectStatus == com.malha.app.domain.model.ProjectStatus.FINISHED ||
                    it.progressPercent >= 100
                } // Concluded
            }

            AnimatedContent(
                targetState = uiState.isLoading to filteredProjects.isEmpty(),
                label = "projects-tab-content"
            ) { (isLoading, isEmpty) ->
                when {
                    isLoading -> Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(stringResource(R.string.loading_projects))
                    }
                    isEmpty -> Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(stringResource(R.string.no_projects_in_this_category), color = colorScheme.onSurfaceVariant)
                    }
                    else -> LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(filteredProjects) { project ->
                            var showMenu by remember { mutableStateOf(false) }
                            var showDuplicateCardDialog by remember { mutableStateOf(false) }
                            var showDeleteDialog by remember { mutableStateOf(false) }

                            val animatedProgress by animateFloatAsState(
                                targetValue = project.progressPercent / 100f,
                                animationSpec = tween(durationMillis = 700),
                                label = "project-list-progress"
                            )
                            val projectDefaultColor = colorScheme.primary
                            val projectColor = project.colorHex?.let { hex ->
                                runCatching { Color(hex.toColorInt()) }.getOrNull()
                            } ?: projectDefaultColor

                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .animateContentSize()
                                    .clickable {
                                        val isOngoing = project.projectStatus != com.malha.app.domain.model.ProjectStatus.FINISHED && project.progressPercent > 0
                                        if (isOngoing) {
                                            onOpenProjectExecution(project.id)
                                        } else {
                                            onOpenProjectSettings(project.id)
                                        }
                                    },
                                colors = CardDefaults.cardColors(
                                    containerColor = projectColor.copy(alpha = 0.15f)
                                )
                            ) {
                                Row(
                                    modifier = Modifier.padding(16.dp),
                                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    ImagePlaceholder(label = project.name, imageUri = project.imageUri)
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(project.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                        Text(
                                            stringResource(
                                                R.string.progress,
                                                project.progressPercent
                                            ),
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                        LinearProgressIndicator(
                                            progress = { animatedProgress },
                                            modifier = Modifier.fillMaxWidth(),
                                            color = projectColor,
                                            trackColor = projectColor.copy(0.2f)
                                        )
                                    }

                                    Box {
                                        IconButton(onClick = { showMenu = true }) {
                                            Icon(
                                                imageVector = Icons.Default.MoreVert,
                                                contentDescription = stringResource(R.string.more_options)
                                            )
                                        }
                                        DropdownMenu(
                                            expanded = showMenu,
                                            onDismissRequest = { showMenu = false }
                                        ) {
                                            DropdownMenuItem(
                                                text = { Text(stringResource(R.string.duplicate_project)) },
                                                onClick = {
                                                    showMenu = false
                                                    showDuplicateCardDialog = true
                                                },
                                                leadingIcon = {
                                                    Icon(
                                                        imageVector = Icons.Default.ContentCopy,
                                                        contentDescription = null
                                                    )
                                                }
                                            )
                                            DropdownMenuItem(
                                                text = { Text(stringResource(R.string.share_project)) },
                                                onClick = {
                                                    showMenu = false
                                                    MalhaShare.shareProject(context, project)
                                                },
                                                leadingIcon = {
                                                    Icon(
                                                        imageVector = androidx.compose.material.icons.Icons.Default.Share,
                                                        contentDescription = null
                                                    )
                                                }
                                            )
                                            DropdownMenuItem(
                                                text = {
                                                    Text(
                                                        stringResource(R.string.delete_project),
                                                        color = MaterialTheme.colorScheme.error
                                                    )
                                                },
                                                onClick = {
                                                    showMenu = false
                                                    showDeleteDialog = true
                                                },
                                                leadingIcon = {
                                                    Icon(
                                                        imageVector = Icons.Default.Delete,
                                                        contentDescription = null,
                                                        tint = MaterialTheme.colorScheme.error
                                                    )
                                                }
                                            )
                                        }
                                    }
                                }
                            }

                            if (showDuplicateCardDialog) {
                                var copyName by remember { mutableStateOf("${project.name} (Copy)") }
                                AlertDialog(
                                    onDismissRequest = { showDuplicateCardDialog = false },
                                    title = { Text(stringResource(R.string.duplicate_project_dialog_title)) },
                                    text = {
                                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
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
                                                viewModel.duplicateProject(project.id, copyName)
                                                showDuplicateCardDialog = false
                                            }
                                        ) {
                                            Text(stringResource(R.string.duplicate_action))
                                        }
                                    },
                                    dismissButton = {
                                        TextButton(onClick = { showDuplicateCardDialog = false }) {
                                            Text(stringResource(R.string.cancel_action))
                                        }
                                    }
                                )
                            }

                            if (showDeleteDialog) {
                                AlertDialog(
                                    onDismissRequest = { showDeleteDialog = false },
                                    title = { Text(stringResource(R.string.delete_project_dialog_title)) },
                                    text = {
                                        Text(
                                            stringResource(
                                                R.string.delete_project_dialog_message,
                                                project.name
                                            )
                                        )
                                    },
                                    confirmButton = {
                                        Button(
                                            onClick = {
                                                viewModel.deleteProject(project.id)
                                                showDeleteDialog = false
                                            },
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = MaterialTheme.colorScheme.error
                                            )
                                        ) {
                                            Text(stringResource(R.string.delete_action))
                                        }
                                    },
                                    dismissButton = {
                                        TextButton(onClick = { showDeleteDialog = false }) {
                                            Text(stringResource(R.string.cancel_action))
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

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
        title = { Text(stringResource(R.string.dialog_new_project_title)) },
        text = {
            OutlinedTextField(
                value = name,
                onValueChange = {
                    name = it
                    localError = null
                },
                label = { Text(stringResource(R.string.input_project_name)) },
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
            val projectNameRequeired = stringResource(R.string.project_name_is_required)
            Button(
                enabled = !isCreating,
                onClick = {
                    if (name.isBlank()) {
                        localError = projectNameRequeired
                    } else {
                        onCreate(name)
                    }
                }
            ) {
                Text(stringResource(R.string.action_create))
            }
        },
        dismissButton = {
            Button(
                enabled = !isCreating,
                onClick = onDismiss
            ) {
                Text(stringResource(R.string.action_cancel))
            }
        }
    )
}
