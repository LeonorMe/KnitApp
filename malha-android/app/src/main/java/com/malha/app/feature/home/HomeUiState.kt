package com.malha.app.feature.home

import com.malha.app.domain.model.Project

data class HomeUiState(
    val isLoading: Boolean = true,
    val projects: List<Project> = emptyList()
) {
    val activeProjectCount: Int = projects.size
    val aidiInsight: String
        get() = when {
            projects.isEmpty() -> "Aidi will help you start your first project when you are ready."
            projects.any { it.progressPercent >= 70 } -> "Aidi noticed a project is close to finished."
            else -> "Aidi is keeping your project progress organized locally."
        }
}

