package com.malha.app.feature.home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.malha.app.core.design.component.ListScreen

@Composable
fun HomeScreen(viewModel: HomeViewModel = viewModel()) {
    val uiState by viewModel.uiState.collectAsState()

    ListScreen(
        title = "Malha",
        subtitle = uiState.aidiInsight,
        items = uiState.projects.map { project ->
            "${project.name} - ${project.progressPercent}% complete"
        },
        emptyText = if (uiState.isLoading) "Loading projects..." else "No active projects yet."
    )
}
