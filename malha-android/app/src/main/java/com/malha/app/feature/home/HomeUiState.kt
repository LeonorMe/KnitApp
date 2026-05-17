package com.malha.app.feature.home

import com.malha.app.domain.model.Project

data class HomeUiState(
    val isLoading: Boolean = true,
    val userName: String = "Ana",
    val coinBalance: Int = 50,
    val projects: List<Project> = emptyList(),
    val aidiSuggestions: List<HomeInsight> = emptyList(),
    val weeklySummary: String = "Esta semana: 3h, 1 ponto novo aprendido",
    val showDailyYarn: Boolean = false
) {
    val activeProjectCount: Int = projects.size
    val aidiSummary: String
        get() = aidiSuggestions.firstOrNull()?.message
            ?: "Aidi is keeping your project progress organized locally."
}

data class HomeInsight(
    val title: String,
    val message: String
)
