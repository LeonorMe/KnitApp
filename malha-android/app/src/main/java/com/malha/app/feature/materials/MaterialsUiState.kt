package com.malha.app.feature.materials

import com.malha.app.domain.model.Material

data class MaterialsUiState(
    val isLoading: Boolean = true,
    val materials: List<Material> = emptyList(),
    val isCreating: Boolean = false,
    val errorMessage: String? = null
)
