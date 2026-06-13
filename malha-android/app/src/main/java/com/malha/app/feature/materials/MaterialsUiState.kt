package com.malha.app.feature.materials

import com.malha.app.domain.model.Material
import com.malha.app.domain.model.Pattern

data class MaterialsUiState(
    val isLoading: Boolean = true,
    val materials: List<Material> = emptyList(),
    val patterns: List<Pattern> = emptyList(),
    val isCreating: Boolean = false,
    val errorMessage: String? = null
)
