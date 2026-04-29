package com.malha.app.feature.projects

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

class ProjectsViewModel(application: Application) : AndroidViewModel(application) {
    private val creationState = MutableStateFlow(ProjectsCreationState())

    val uiState: StateFlow<ProjectsUiState> = appContainer.projectRepository
        .observeActiveProjects()
        .combine(creationState) { projects, creation ->
            ProjectsUiState(
                isLoading = false,
                projects = projects,
                isCreating = creation.isCreating,
                errorMessage = creation.errorMessage
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = ProjectsUiState()
        )

    init {
        viewModelScope.launch {
            appContainer.databaseSeeder.seedIfEmpty()
        }
    }

    fun createProject(name: String) {
        val trimmedName = name.trim()
        if (trimmedName.isBlank()) {
            creationState.update { it.copy(errorMessage = "Project name is required.") }
            return
        }

        viewModelScope.launch {
            creationState.update { it.copy(isCreating = true, errorMessage = null) }
            runCatching {
                val patternId = appContainer.patternRepository.getFirstPatternId()
                appContainer.projectRepository.createProject(
                    name = trimmedName,
                    patternId = patternId
                )
            }.onFailure { error ->
                creationState.update {
                    it.copy(errorMessage = error.message ?: "Could not create project.")
                }
            }
            creationState.update { it.copy(isCreating = false) }
        }
    }

    fun clearError() {
        creationState.update { it.copy(errorMessage = null) }
    }
}

private data class ProjectsCreationState(
    val isCreating: Boolean = false,
    val errorMessage: String? = null
)
