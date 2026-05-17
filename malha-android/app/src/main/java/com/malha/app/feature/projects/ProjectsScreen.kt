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
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import com.malha.app.R
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.clickable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.malha.app.core.design.component.ImagePlaceholder

@Composable
fun ProjectsScreen(
    onOpenProject: (String) -> Unit,
    viewModel: ProjectsViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showCreateDialog by remember { mutableStateOf(false) }

    val tabs = listOf("Em curso", "Planeados", "Concluídos", "Importados")
    var selectedTabIndex by remember { mutableStateOf(0) }

    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = { Text("Os Meus Projetos") },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.background
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
            FloatingActionButton(onClick = {
                viewModel.clearError()
                showCreateDialog = true
            }) {
                Icon(Icons.Default.Add, contentDescription = "Novo Projeto")
            }
        }
    ) { padding ->
        Surface(
            modifier = Modifier.fillMaxSize().padding(padding),
            color = MaterialTheme.colorScheme.background
        ) {
            if (uiState.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
                    Text("Loading projects...")
                }
            } else {
                val filteredProjects = when (selectedTabIndex) {
                    0 -> uiState.projects // "Em curso" mock
                    1 -> emptyList() // "Planeados" mock
                    2 -> emptyList() // "Concluídos" mock
                    else -> emptyList() // "Importados" mock
                }

                if (filteredProjects.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
                        Text("No projects in this category.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(filteredProjects) { project ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onOpenProject(project.id) }
                            ) {
                                Row(
                                    modifier = Modifier.padding(16.dp),
                                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                                ) {
                                    ImagePlaceholder(label = project.name, imageUri = project.imageUri)
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(project.name, style = MaterialTheme.typography.titleMedium)
                                        Text("Progresso: ${project.progressPercent}%", style = MaterialTheme.typography.bodyMedium)
                                        Spacer(modifier = Modifier.height(8.dp))
                                        LinearProgressIndicator(
                                            progress = { project.progressPercent / 100f },
                                            modifier = Modifier.fillMaxWidth()
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
