package com.malha.app.feature.patterns

import com.malha.app.domain.model.Pattern

data class PatternsUiState(
    val isLoading: Boolean = true,
    val patterns: List<Pattern> = emptyList()
)

