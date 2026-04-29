package com.malha.app.feature.patterns

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.malha.app.core.design.component.ListScreen

@Composable
fun PatternsScreen(viewModel: PatternsViewModel = viewModel()) {
    val uiState by viewModel.uiState.collectAsState()

    ListScreen(
        title = "Patterns",
        subtitle = "Manual pattern input and the first starter patterns will live here.",
        items = uiState.patterns.map { pattern ->
            "${pattern.title} - ${pattern.steps.size} steps"
        },
        emptyText = if (uiState.isLoading) "Loading patterns..." else "No patterns yet."
    )
}
