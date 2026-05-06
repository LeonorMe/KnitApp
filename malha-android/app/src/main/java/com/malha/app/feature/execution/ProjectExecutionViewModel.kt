package com.malha.app.feature.execution

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.malha.app.core.app.appContainer
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class ProjectExecutionViewModel(
    application: Application,
    private val projectId: String
) : AndroidViewModel(application) {
    private val errorMessage = MutableStateFlow<String?>(null)
    private val projectFlow = appContainer.projectRepository.observeProject(projectId)
    private val patternFlow = projectFlow.flatMapLatest { project ->
        val patternId = project?.patternId
        if (patternId == null) {
            flowOf(null)
        } else {
            appContainer.patternRepository.observePattern(patternId)
        }
    }
    private val currentStepProgressFlow = combine(projectFlow, patternFlow) { project, pattern ->
        val currentStep = pattern?.steps?.getOrNull(project?.currentStepIndex ?: 0)
        Pair(project, currentStep)
    }.flatMapLatest { (project, currentStep) ->
        if (project == null || currentStep == null) {
            flowOf(null)
        } else {
            appContainer.projectRepository.observeStepProgress(
                projectId = project.id,
                patternStepId = currentStep.id
            )
        }
    }

    val uiState: StateFlow<ProjectExecutionUiState> = combine(
        projectFlow,
        patternFlow,
        currentStepProgressFlow,
        errorMessage
    ) { project, pattern, stepProgress, error ->
        ProjectExecutionUiState(
            isLoading = false,
            project = project,
            pattern = pattern,
            currentStepProgress = stepProgress,
            errorMessage = error
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = ProjectExecutionUiState()
    )

    fun previousStep() {
        val state = uiState.value
        val project = state.project ?: return
        val nextIndex = (project.currentStepIndex - 1).coerceAtLeast(0)
        viewModelScope.launch {
            updateStep(nextIndex, state.totalSteps)
        }
    }

    fun nextStep() {
        val state = uiState.value
        val project = state.project ?: return
        if (state.totalSteps == 0) return
        val nextIndex = (project.currentStepIndex + 1).coerceAtMost(state.totalSteps - 1)
        viewModelScope.launch {
            updateStep(nextIndex, state.totalSteps)
        }
    }

    fun markCurrentStepDone() {
        val state = uiState.value
        val project = state.project ?: return
        val currentStep = state.currentStep ?: return
        if (state.totalSteps == 0) return

        val nextIndex = if (project.currentStepIndex < state.totalSteps - 1) {
            project.currentStepIndex + 1
        } else {
            project.currentStepIndex
        }
        viewModelScope.launch {
            runCatching {
                appContainer.projectRepository.saveStepProgress(
                    projectId = project.id,
                    patternStepId = currentStep.id,
                    isDone = true,
                    note = state.currentNote
                )
                updateStep(nextIndex, state.totalSteps)
            }.onFailure { error ->
                errorMessage.update {
                    error.message ?: "Could not mark step done."
                }
            }
        }
    }

    fun saveCurrentStepNote(note: String) {
        val state = uiState.value
        val project = state.project ?: return
        val currentStep = state.currentStep ?: return

        viewModelScope.launch {
            runCatching {
                appContainer.projectRepository.saveStepProgress(
                    projectId = project.id,
                    patternStepId = currentStep.id,
                    isDone = state.isCurrentStepDone,
                    note = note
                )
            }.onFailure { error ->
                errorMessage.update {
                    error.message ?: "Could not save note."
                }
            }
        }
    }

    private suspend fun updateStep(stepIndex: Int, totalSteps: Int) {
        val progressPercent = if (totalSteps <= 0) {
            0
        } else {
            (((stepIndex + 1).toFloat() / totalSteps.toFloat()) * 100).toInt()
        }

        runCatching {
            appContainer.projectRepository.updateProgress(
                projectId = projectId,
                stepIndex = stepIndex,
                progressPercent = progressPercent
            )
        }.onFailure { error ->
            errorMessage.update {
                error.message ?: "Could not update project progress."
            }
        }
    }

    fun clearError() {
        errorMessage.update { null }
    }

    companion object {
        fun factory(projectId: String): ViewModelProvider.Factory {
            return object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(
                    modelClass: Class<T>,
                    extras: CreationExtras
                ): T {
                    val application = checkNotNull(extras[APPLICATION_KEY])
                    return ProjectExecutionViewModel(application, projectId) as T
                }
            }
        }
    }
}
