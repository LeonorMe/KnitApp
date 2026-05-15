package com.malha.app.feature.execution

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.malha.app.core.app.appContainer
import com.malha.app.domain.model.Pattern
import com.malha.app.domain.model.PatternStep
import com.malha.app.domain.model.Project
import com.malha.app.domain.model.ProjectStepProgress
import com.malha.app.MalhaApplication
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
    private val selectedSizeState = MutableStateFlow<String?>(null)
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
        val currentStep = pattern?.allSteps?.getOrNull(project?.currentStepIndex ?: 0)
        project to currentStep
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
        selectedSizeState,
        errorMessage
    ) { project: Project?, pattern: Pattern?, stepProgress: ProjectStepProgress?, selectedSize: String?, error: String? ->
        ProjectExecutionUiState(
            isLoading = false,
            project = project,
            pattern = pattern,
            currentStepProgress = stepProgress,
            selectedSize = selectedSize ?: pattern?.selectedSize,
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
            appContainer.projectRepository.saveStepProgress(
                projectId = project.id,
                patternStepId = currentStep.id,
                isDone = true,
                note = state.currentStepProgress?.note
            )
            updateStep(nextIndex, state.totalSteps)
        }
    }

    private suspend fun updateStep(index: Int, total: Int) {
        appContainer.projectRepository.updateProgress(projectId, index, (index.toFloat() / total.toFloat() * 100).toInt())
    }

    fun selectSize(size: String) {
        selectedSizeState.update { size }
    }

    fun clearError() {
        errorMessage.update { null }
    }

    companion object {
        fun factory(projectId: String) = object : androidx.lifecycle.ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                val app = MalhaApplication.instance
                return ProjectExecutionViewModel(app, projectId) as T
            }
        }
    }
}
