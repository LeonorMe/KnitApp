package com.malha.app.feature.materials

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.malha.app.core.app.appContainer
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MaterialsViewModel(application: Application) : AndroidViewModel(application) {
    val uiState: StateFlow<MaterialsUiState> = appContainer.materialRepository
        .observeMaterials()
        .map { materials -> MaterialsUiState(isLoading = false, materials = materials) }
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
}

