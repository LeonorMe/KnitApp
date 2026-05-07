package com.malha.app.feature.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.malha.app.core.app.appContainer
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HomeViewModel(application: Application) : AndroidViewModel(application) {
    val uiState: StateFlow<HomeUiState> = appContainer.projectRepository
        .observeActiveProjects()
        .combine(appContainer.materialRepository.observeMaterials()) { projects, materials ->
            HomeUiState(
                isLoading = false,
                projects = projects,
                insights = HomeInsightEngine.buildInsights(
                    projects = projects,
                    materials = materials
                )
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = HomeUiState()
        )

    init {
        viewModelScope.launch {
            appContainer.databaseSeeder.seedIfEmpty()
        }
    }
}
