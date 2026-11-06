package com.malha.app.feature.execution

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.malha.app.MalhaApplication
import com.malha.app.core.app.appContainer
import com.malha.app.core.util.SocialDefaults.COINS_PER_PROJECT
import com.malha.app.domain.engine.IncrementSuggestionPolicy
import com.malha.app.domain.engine.ProgressCalculator
import com.malha.app.domain.model.Pattern
import com.malha.app.domain.model.Project
import com.malha.app.domain.model.ProjectStepProgress
import com.malha.app.domain.pattern.InstructionProgress
import com.malha.app.domain.pattern.ProjectInstructionSnapshot
import com.malha.app.domain.pattern.ProjectPatternSnapshot
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
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
        val patternId = project?.patternId ?: return@flatMapLatest flowOf(null)
        appContainer.patternRepository.observePattern(patternId)
    }

    private val snapshotFlow = appContainer.projectSnapshotRepository.observeSnapshot(projectId)

    private val instructionsFlow = snapshotFlow.flatMapLatest { snapshot ->
        if (snapshot == null) flowOf(emptyList())
        else appContainer.projectSnapshotRepository.observeInstructions(snapshot.id)
    }

    private val progressListFlow = appContainer.projectSnapshotRepository.observeProgress(projectId)

    // Legacy fallback progress flow
    private val currentStepProgressFlow = combine(projectFlow, patternFlow) { project, pattern ->
        val currentStep = pattern?.allSteps?.getOrNull(project?.currentStepIndex ?: 0)
        project to currentStep
    }.flatMapLatest { (project, currentStep) ->
        if (project == null || currentStep == null) return@flatMapLatest flowOf(null)
        appContainer.projectRepository.observeStepProgress(
            projectId = project.id,
            patternStepId = currentStep.id
        )
    }

    val uiState: StateFlow<ProjectExecutionUiState> = combine(
        projectFlow,
        patternFlow,
        snapshotFlow,
        instructionsFlow,
        progressListFlow,
        currentStepProgressFlow,
        selectedSizeState,
        errorMessage
    ) { args: Array<Any?> ->
        val project = args[0] as? Project
        val pattern = args[1] as? Pattern
        val snapshot = args[2] as? ProjectPatternSnapshot
        @Suppress("UNCHECKED_CAST")
        val instructions = args[3] as? List<ProjectInstructionSnapshot> ?: emptyList()
        @Suppress("UNCHECKED_CAST")
        val progressList = args[4] as? List<InstructionProgress> ?: emptyList()
        val stepProgress = args[5] as? ProjectStepProgress
        val selectedSize = args[6] as? String
        val error = args[7] as? String

        ProjectExecutionUiState(
            isLoading = false,
            project = project,
            pattern = pattern,
            snapshot = snapshot,
            instructions = instructions,
            progressList = progressList,
            currentStepProgress = stepProgress,
            selectedSize = selectedSize ?: project?.selectedSize ?: pattern?.selectedSize,
            errorMessage = error
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = ProjectExecutionUiState()
    )

    init {
        // Ensure snapshot exists for active project
        viewModelScope.launch {
            projectFlow.filterNotNull().firstOrNull()?.let { project ->
                val pId = project.patternId
                val size = project.selectedSize ?: "Default"
                val existingSnapshot = appContainer.projectSnapshotRepository.getSnapshot(projectId)
                if (existingSnapshot == null && !pId.isNullOrBlank()) {
                    runCatching {
                        appContainer.projectSnapshotRepository.createAndSaveSnapshot(
                            projectId = projectId,
                            patternId = pId,
                            sizeKey = size
                        )
                    }
                }
            }
        }
    }

    fun incrementCounter() {
        val state = uiState.value
        val project = state.project ?: return

        if (state.isSnapshotMode) {
            val instruction = state.currentInstruction ?: return
            val target = instruction.targetCount ?: 1
            val current = state.currentCount
            val inc = state.selectedIncrement
            val next = IncrementSuggestionPolicy.nextCount(current, inc, target)

            viewModelScope.launch {
                appContainer.projectSnapshotRepository.updateCount(
                    projectId = project.id,
                    instructionId = instruction.id,
                    count = next,
                    increment = inc
                )
            }
        } else {
            // Legacy fallback
            val target = state.targetCount ?: 1
            val next = minOf(state.currentCount + 1, target)
            // handled locally in screen or legacy step
        }
    }

    fun decrementCounter() {
        val state = uiState.value
        val project = state.project ?: return

        if (state.isSnapshotMode) {
            val instruction = state.currentInstruction ?: return
            val current = state.currentCount
            val inc = state.selectedIncrement
            val prev = IncrementSuggestionPolicy.previousCount(current, inc)

            viewModelScope.launch {
                appContainer.projectSnapshotRepository.updateCount(
                    projectId = project.id,
                    instructionId = instruction.id,
                    count = prev,
                    increment = inc
                )
            }
        }
    }

    fun setIncrement(increment: Int) {
        if (increment <= 0) return
        val state = uiState.value
        val project = state.project ?: return
        val instruction = state.currentInstruction ?: return

        viewModelScope.launch {
            appContainer.projectSnapshotRepository.updateCount(
                projectId = project.id,
                instructionId = instruction.id,
                count = state.currentCount,
                increment = increment
            )
        }
    }

    fun markCurrentStepDone() {
        val state = uiState.value
        val project = state.project ?: return
        if (state.totalSteps == 0) return

        val isLastStep = (project.currentStepIndex >= state.totalSteps - 1)
        val nextIndex = if (!isLastStep) project.currentStepIndex + 1 else project.currentStepIndex

        viewModelScope.launch {
            if (state.isSnapshotMode) {
                val instruction = state.currentInstruction ?: return@launch
                appContainer.projectSnapshotRepository.markCompleted(
                    projectId = project.id,
                    instructionId = instruction.id,
                    completed = true
                )

                // Re-calculate progress percent: completed / total
                val currentCompletedCount = state.progressList.count { it.isCompleted || it.instructionSnapshotId == instruction.id }
                val newProgress = ProgressCalculator.calculatePercentage(currentCompletedCount, state.totalSteps)
                val maxProgress = maxOf(project.progressPercent, newProgress)
                appContainer.projectRepository.updateProgress(projectId, nextIndex, maxProgress)
            } else {
                // Legacy
                val currentStep = state.currentStep ?: return@launch
                appContainer.projectRepository.saveStepProgress(
                    projectId = project.id,
                    patternStepId = currentStep.id,
                    isDone = true,
                    note = state.currentStepProgress?.note
                )
                val completedCount = if (isLastStep) state.totalSteps else nextIndex
                val newProgress = ProgressCalculator.calculatePercentage(completedCount, state.totalSteps)
                val maxProgress = maxOf(project.progressPercent, newProgress)
                appContainer.projectRepository.updateProgress(projectId, nextIndex, maxProgress)
            }
        }
    }

    fun undoPreviousStep() {
        val state = uiState.value
        val project = state.project ?: return
        if (project.currentStepIndex <= 0) return

        val prevIndex = project.currentStepIndex - 1

        viewModelScope.launch {
            if (state.isSnapshotMode) {
                val prevInstruction = state.instructions.getOrNull(prevIndex)
                if (prevInstruction != null) {
                    appContainer.projectSnapshotRepository.markCompleted(
                        projectId = project.id,
                        instructionId = prevInstruction.id,
                        completed = false
                    )
                }
                val newCompletedCount = state.progressList.count { it.isCompleted && it.instructionSnapshotId != prevInstruction?.id }
                val newProgress = ProgressCalculator.calculatePercentage(newCompletedCount, state.totalSteps)
                appContainer.projectRepository.updateProgress(projectId, prevIndex, newProgress)
            } else {
                val currentStep = state.allSteps.getOrNull(prevIndex)
                if (currentStep != null) {
                    appContainer.projectRepository.saveStepProgress(
                        projectId = project.id,
                        patternStepId = currentStep.id,
                        isDone = false,
                        note = null
                    )
                }
                val newProgress = ProgressCalculator.calculatePercentage(prevIndex, state.totalSteps)
                appContainer.projectRepository.updateProgress(projectId, prevIndex, newProgress)
            }
        }
    }

    fun jumpToStep(index: Int) {
        val state = uiState.value
        val project = state.project ?: return
        if (index < 0 || index >= state.totalSteps) return

        viewModelScope.launch {
            appContainer.projectRepository.updateProgress(projectId, index, project.progressPercent)
        }
    }

    fun previousStep() {
        val state = uiState.value
        val project = state.project ?: return
        val prevIndex = (project.currentStepIndex - 1).coerceAtLeast(0)
        viewModelScope.launch {
            appContainer.projectRepository.updateProgress(projectId, prevIndex, project.progressPercent)
        }
    }

    fun nextStep() {
        val state = uiState.value
        val project = state.project ?: return
        if (state.totalSteps == 0) return
        val nextIndex = (project.currentStepIndex + 1).coerceAtMost(state.totalSteps - 1)
        viewModelScope.launch {
            appContainer.projectRepository.updateProgress(projectId, nextIndex, project.progressPercent)
        }
    }

    fun saveNote(note: String) {
        val state = uiState.value
        val project = state.project ?: return

        viewModelScope.launch {
            runCatching {
                if (state.isSnapshotMode) {
                    val instruction = state.currentInstruction ?: return@launch
                    appContainer.projectSnapshotRepository.saveNote(
                        projectId = project.id,
                        instructionId = instruction.id,
                        note = note.trim().takeIf { it.isNotBlank() }
                    )
                } else {
                    val currentStep = state.currentStep ?: return@launch
                    appContainer.projectRepository.saveStepProgress(
                        projectId = project.id,
                        patternStepId = currentStep.id,
                        isDone = state.currentStepProgress?.isDone ?: false,
                        note = note.trim().takeIf { it.isNotBlank() }
                    )
                }
            }.onFailure { error ->
                errorMessage.update { error.message ?: "Could not save note." }
            }
        }
    }

    fun finishProject(photoUri: String?, publishToFeed: Boolean = true) {
        val state = uiState.value
        val project = state.project ?: return

        viewModelScope.launch {
            runCatching {
                appContainer.projectRepository.completeProject(project.id, photoUri)
                appContainer.socialRepository.addCoins(COINS_PER_PROJECT)

                val effectivePhoto = photoUri ?: project.imageUri
                if (publishToFeed && !effectivePhoto.isNullOrBlank()) {
                    appContainer.socialRepository.createPost(
                        patternId = project.patternId,
                        imageUri = effectivePhoto,
                        description = "Finished project: ${project.name} 🎉",
                        status = "completed"
                    )
                }
            }.onFailure { error ->
                errorMessage.update { error.message ?: "Could not finish project." }
            }
        }
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
