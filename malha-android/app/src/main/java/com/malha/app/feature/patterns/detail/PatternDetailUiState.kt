package com.malha.app.feature.patterns.detail

import com.malha.app.domain.model.Pattern

data class PatternDetailUiState(
    val isLoading: Boolean = true,
    val pattern: Pattern? = null,
    val isStartingProject: Boolean = false,
    val startedProjectId: String? = null,
    val errorMessage: String? = null
)

