package com.malha.app.feature.projects

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.malha.app.core.app.appContainer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProjectsViewModel(application: Application) : AndroidViewModel(application) {
    private val creationState = MutableStateFlow(ProjectsCreationState())

    val uiState: StateFlow<ProjectsUiState> = appContainer.projectRepository
        .observeAllProjects()
        .combine(creationState) { projects, creation ->
            ProjectsUiState(
                isLoading = false,
                projects = projects,
                isCreating = creation.isCreating,
                errorMessage = creation.errorMessage,
                createdProjectId = creation.createdProjectId
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = ProjectsUiState()
        )

    init {
        viewModelScope.launch {
            appContainer.databaseSeeder.seed()
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
                // Step 1: User provides name, pattern is configured in Step 2 (Project Configuration)
                appContainer.projectRepository.createProject(
                    name = trimmedName,
                    patternId = null
                )
            }.onSuccess { created ->
                creationState.update {
                    it.copy(
                        isCreating = false,
                        createdProjectId = created
                    )
                }
            }.onFailure { error ->
                creationState.update {
                    it.copy(
                        isCreating = false,
                        errorMessage = error.message ?: "Could not create project.")
                }
            }
        }
    }

    fun duplicateProject(projectId: String, newName: String? = null) {
        viewModelScope.launch {
            runCatching {
                appContainer.projectRepository.duplicateProject(projectId, newName)
            }.onFailure { error ->
                creationState.update {
                    it.copy(errorMessage = error.message ?: "Could not duplicate project.")
                }
            }
        }
    }

    fun deleteProject(projectId: String) {
        viewModelScope.launch {
            runCatching {
                appContainer.projectRepository.deleteProject(projectId)
            }.onFailure { error ->
                creationState.update {
                    it.copy(errorMessage = error.message ?: "Could not delete project.")
                }
            }
        }
    }

    fun consumeCreatedProjectId() {
        creationState.update { it.copy(createdProjectId = null) }
    }

    fun clearError() {
        creationState.update { it.copy(errorMessage = null) }
    }
}

private data class ProjectsCreationState(
    val isCreating: Boolean = false,
    val errorMessage: String? = null,
    val createdProjectId: String? = null
)

