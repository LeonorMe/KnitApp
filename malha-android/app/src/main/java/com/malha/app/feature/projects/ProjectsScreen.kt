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
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
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
import androidx.compose.runtime.mutableIntStateOf
import com.malha.app.R
import com.malha.app.core.design.component.ImagePlaceholder
import com.malha.app.core.share.MalhaShare

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectsScreen(
    onOpenProject: (String) -> Unit,
    viewModel: ProjectsViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    var showCreateDialog by remember { mutableStateOf(false) }

    val tabs = listOf(stringResource(R.string.project_on_going),
        stringResource(R.string.project_planned), stringResource(R.string.project_concluded)
    )
    var selectedTabIndex by remember { mutableIntStateOf(0) }

    Scaffold(
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
                    it.progressPercent in 1..99
                } // Ongoing

                1 -> uiState.projects.filter {
                    it.progressPercent == 0
                } // Planned

                else -> uiState.projects.filter {
                    it.progressPercent >= 100
                } // Completed
            }

            AnimatedContent(
                targetState = uiState.isLoading to filteredProjects.isEmpty(),
                label = "projects-tab-content"
            ) { (isLoading, isEmpty) ->
                when {
                    isLoading -> Box(modifier = Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
                        Text(stringResource(R.string.loading_projects))
                    }
                    isEmpty -> Box(modifier = Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
                        Text(stringResource(R.string.no_projects_in_this_category), color = colorScheme.onSurfaceVariant)
                    }
                    else -> LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(filteredProjects) { project ->
                            val animatedProgress by animateFloatAsState(
                                targetValue = project.progressPercent / 100f,
                                animationSpec = tween(durationMillis = 700),
                                label = "project-list-progress" // todo string
                            )
                            val projectDefaultColor = colorScheme.primary
                            val projectColor = project.colorHex?.let { hex ->
                                    runCatching {
                                        Color(android.graphics.Color.parseColor(hex))
                                    }.getOrNull()
                                } ?: projectDefaultColor

                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .animateContentSize()
                                    .clickable { onOpenProject(project.id) },
                                colors = CardDefaults.cardColors(
                                        containerColor = projectColor.copy(alpha = 0.15f)
                                )
                            ) {
                                Row(
                                    modifier = Modifier.padding(16.dp),
                                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                                ) {
                                    ImagePlaceholder(label = project.name, imageUri = project.imageUri)
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(project.name, style = MaterialTheme.typography.titleMedium)
                                        Text(
                                            stringResource(
                                                R.string.progress,
                                                project.progressPercent
                                            ), style = MaterialTheme.typography.bodyMedium)
                                        Spacer(modifier = Modifier.height(8.dp))
                                        LinearProgressIndicator(
                                            progress = { animatedProgress },
                                            modifier = Modifier.fillMaxWidth(),
                                            color = projectColor,
                                            trackColor = projectColor.copy(0.2f)
                                        )
                                    }
                                    IconButton(onClick = { MalhaShare.shareProject(context, project) }) {
                                        Icon(Icons.Default.Share, contentDescription = stringResource(
                                            R.string.share_project
                                        ))
                                    }
                                }
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
