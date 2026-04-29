package com.malha.app.feature.projects

import com.malha.app.domain.model.Project

data class ProjectsUiState(
    val isLoading: Boolean = true,
    val projects: List<Project> = emptyList(),
    val isCreating: Boolean = false,
    val errorMessage: String? = null
)
