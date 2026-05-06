package com.malha.app.feature.patterns.detail

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.malha.app.core.app.appContainer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PatternDetailViewModel(
    application: Application,
    private val patternId: String
) : AndroidViewModel(application) {
    private val startState = MutableStateFlow(PatternStartState())

    val uiState: StateFlow<PatternDetailUiState> = appContainer.patternRepository
        .observePattern(patternId)
        .combine(startState) { pattern, start ->
            PatternDetailUiState(
                isLoading = false,
                pattern = pattern,
                isStartingProject = start.isStartingProject,
                startedProjectId = start.startedProjectId,
                errorMessage = start.errorMessage
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = PatternDetailUiState()
        )

    fun startProject(projectName: String) {
        val trimmedName = projectName.trim()
        if (trimmedName.isBlank()) {
            startState.update { it.copy(errorMessage = "Project name is required.") }
            return
        }

        viewModelScope.launch {
            startState.update {
                it.copy(isStartingProject = true, startedProjectId = null, errorMessage = null)
            }
            runCatching {
                appContainer.projectRepository.createProject(
                    name = trimmedName,
                    patternId = patternId
                )
            }.onSuccess { projectId ->
                startState.update {
                    it.copy(startedProjectId = projectId)
                }
            }.onFailure { error ->
                startState.update {
                    it.copy(errorMessage = error.message ?: "Could not start project.")
                }
            }
            startState.update { it.copy(isStartingProject = false) }
        }
    }

    fun clearError() {
        startState.update { it.copy(errorMessage = null) }
    }

    companion object {
        fun factory(patternId: String): ViewModelProvider.Factory {
            return object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(
                    modelClass: Class<T>,
                    extras: CreationExtras
                ): T {
                    val application = checkNotNull(extras[APPLICATION_KEY])
                    return PatternDetailViewModel(application, patternId) as T
                }
            }
        }
    }
}

private data class PatternStartState(
    val isStartingProject: Boolean = false,
    val startedProjectId: String? = null,
    val errorMessage: String? = null
)
