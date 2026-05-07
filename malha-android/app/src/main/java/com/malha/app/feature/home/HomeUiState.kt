package com.malha.app.feature.home

import com.malha.app.domain.model.Project

data class HomeUiState(
    val isLoading: Boolean = true,
    val projects: List<Project> = emptyList(),
    val insights: List<HomeInsight> = emptyList()
) {
    val activeProjectCount: Int = projects.size
    val aidiSummary: String
        get() = insights.firstOrNull()?.message
            ?: "Aidi is keeping your project progress organized locally."
}

data class HomeInsight(
    val title: String,
    val message: String
)
