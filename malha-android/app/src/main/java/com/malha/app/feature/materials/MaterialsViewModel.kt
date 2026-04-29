package com.malha.app.feature.materials

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.malha.app.core.app.appContainer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
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

    fun createYarn(name: String, quantityText: String, unit: String) {
        val trimmedName = name.trim()
        val trimmedUnit = unit.trim().ifBlank { "skeins" }
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
                appContainer.materialRepository.createYarn(
                    name = trimmedName,
                    quantity = quantity,
                    unit = trimmedUnit
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
}

private data class MaterialsCreationState(
    val isCreating: Boolean = false,
    val errorMessage: String? = null
)
