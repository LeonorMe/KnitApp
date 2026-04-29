package com.malha.app.feature.materials

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.malha.app.core.design.component.ListScreen

@Composable
fun MaterialsScreen(viewModel: MaterialsViewModel = viewModel()) {
    val uiState by viewModel.uiState.collectAsState()

    ListScreen(
        title = "Materials",
        subtitle = "Track yarn, needles, hooks, purchases, and project assignments.",
        items = uiState.materials.map { material ->
            "${material.name} - ${material.quantity} ${material.unit}"
        },
        emptyText = if (uiState.isLoading) "Loading materials..." else "No materials yet."
    )
}
