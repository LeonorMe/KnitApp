package com.malha.app.feature.materials

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.malha.app.core.app.appContainer
import com.malha.app.domain.model.MaterialType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MaterialsViewModel(application: Application) : AndroidViewModel(application) {
    private val creationState = MutableStateFlow(MaterialsCreationState())

    val uiState: StateFlow<MaterialsUiState> = appContainer.materialRepository
        .observeMaterials()
        .combine(creationState) { materials, creation ->
            MaterialsUiState(
                isLoading = false,
                materials = materials,
                isCreating = creation.isCreating,
                errorMessage = creation.errorMessage
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = MaterialsUiState()
        )

    init {
        viewModelScope.launch {
            appContainer.databaseSeeder.seedIfEmpty()
        }
    }

    fun createMaterial(
        name: String,
        type: MaterialType,
        quantityText: String,
        unit: String,
        imageUri: String? = null
    ) {
        val trimmedName = name.trim()
        val trimmedUnit = unit.trim().ifBlank { defaultUnitFor(type) }
        val quantity = quantityText.trim().replace(',', '.').toDoubleOrNull()

        when {
            trimmedName.isBlank() -> {
                creationState.update { it.copy(errorMessage = "Material name is required.") }
                return
            }
            quantity == null || quantity <= 0.0 -> {
                creationState.update { it.copy(errorMessage = "Quantity must be greater than 0.") }
                return
            }
        }

        viewModelScope.launch {
            creationState.update { it.copy(isCreating = true, errorMessage = null) }
            runCatching {
                appContainer.materialRepository.createMaterial(
                    name = trimmedName,
                    type = type,
                    quantity = quantity,
                    unit = trimmedUnit,
                    imageUri = imageUri
                )
            }.onFailure { error ->
                creationState.update {
                    it.copy(errorMessage = error.message ?: "Could not create material.")
                }
            }
            creationState.update { it.copy(isCreating = false) }
        }
    }

    fun clearError() {
        creationState.update { it.copy(errorMessage = null) }
    }

    private fun defaultUnitFor(type: MaterialType): String {
        return when (type) {
            MaterialType.Yarn -> "skeins"
            MaterialType.Needle -> "pairs"
            MaterialType.Hook -> "hooks"
            MaterialType.Accessory -> "items"
        }
    }
}

private data class MaterialsCreationState(
    val isCreating: Boolean = false,
    val errorMessage: String? = null
)
