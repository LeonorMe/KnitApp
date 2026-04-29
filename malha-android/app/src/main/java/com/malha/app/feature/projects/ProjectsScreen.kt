package com.malha.app.feature.projects

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.malha.app.core.design.component.ListScreen

@Composable
fun ProjectsScreen(viewModel: ProjectsViewModel = viewModel()) {
    val uiState by viewModel.uiState.collectAsState()

    ListScreen(
        title = "Projects",
        subtitle = "Create projects, resume the current step, and track handmade progress.",
        items = uiState.projects.map { project ->
            "${project.name} - step ${project.currentStepIndex + 1}"
        },
        emptyText = if (uiState.isLoading) "Loading projects..." else "No projects yet."
    )
}
