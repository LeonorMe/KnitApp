package com.malha.app.feature.projects

import com.malha.app.domain.model.Project

data class ProjectsUiState(
    val projects: List<Project> = emptyList(),
    val isLoading: Boolean = true,
    val isCreating: Boolean = false,
    val errorMessage: String? = null,
    val createdProjectId: String? = null // NEW: one-shot signal
)
