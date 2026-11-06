package com.malha.app.feature.execution

import com.malha.app.domain.engine.IncrementSuggestionPolicy
import com.malha.app.domain.engine.ProgressCalculator
import com.malha.app.domain.model.Pattern
import com.malha.app.domain.model.PatternStep
import com.malha.app.domain.model.Project
import com.malha.app.domain.model.ProjectStepProgress
import com.malha.app.domain.pattern.InstructionProgress
import com.malha.app.domain.pattern.ProjectInstructionSnapshot
import com.malha.app.domain.pattern.ProjectPatternSnapshot

data class ProjectExecutionUiState(
    val isLoading: Boolean = true,
    val project: Project? = null,
    val pattern: Pattern? = null,
    val snapshot: ProjectPatternSnapshot? = null,
    val instructions: List<ProjectInstructionSnapshot> = emptyList(),
    val progressList: List<InstructionProgress> = emptyList(),
    val currentStepProgress: ProjectStepProgress? = null,
    val errorMessage: String? = null,
    val selectedSize: String? = null,
    val customIncrementInput: String? = null
) {
    val isSnapshotMode: Boolean
        get() = instructions.isNotEmpty()

    val currentInstruction: ProjectInstructionSnapshot?
        get() {
            val index = project?.currentStepIndex ?: 0
            return instructions.getOrNull(index.coerceIn(0, (instructions.size - 1).coerceAtLeast(0)))
        }

    val currentInstructionProgress: InstructionProgress?
        get() = currentInstruction?.let { inst ->
            progressList.firstOrNull { it.instructionSnapshotId == inst.id }
        }

    val currentCount: Int
        get() = currentInstructionProgress?.currentCount ?: 0

    val selectedIncrement: Int
        get() = currentInstructionProgress?.selectedIncrement ?: 1

    val targetCount: Int?
        get() = currentInstruction?.targetCount ?: currentStep?.stitchCountData?.toIntOrNull()

    val suggestedIncrements: List<Int>
        get() = targetCount?.let { IncrementSuggestionPolicy.suggestIncrements(it) } ?: listOf(1)

    // Legacy fallback
    val allSteps: List<PatternStep>
        get() = pattern?.allSteps?.filter { step ->
            step.condition == null || selectedSize == null || step.condition.contains(selectedSize)
        } ?: emptyList()

    val currentStep: PatternStep?
        get() {
            val steps = allSteps
            val index = project?.currentStepIndex ?: 0
            return steps.getOrNull(index.coerceIn(0, (steps.size - 1).coerceAtLeast(0)))
        }

    val currentStepNumber: Int
        get() = (project?.currentStepIndex ?: 0) + 1

    val totalSteps: Int
        get() = if (isSnapshotMode) instructions.size else allSteps.size

    val canGoPrevious: Boolean
        get() = (project?.currentStepIndex ?: 0) > 0

    val canGoNext: Boolean
        get() = totalSteps > 0 && (project?.currentStepIndex ?: 0) < totalSteps - 1

    val isCurrentStepDone: Boolean
        get() = if (isSnapshotMode) {
            currentInstructionProgress?.isCompleted == true
        } else {
            currentStepProgress?.isDone == true
        }

    val currentNote: String?
        get() = if (isSnapshotMode) currentInstructionProgress?.note else currentStepProgress?.note

    val completedStepCount: Int
        get() = if (isSnapshotMode) {
            progressList.count { it.isCompleted }
        } else {
            val idx = (project?.currentStepIndex ?: 0)
            if (isCurrentStepDone) (idx + 1).coerceAtMost(totalSteps) else idx.coerceAtMost(totalSteps)
        }

    val progressPercent: Int
        get() = ProgressCalculator.calculatePercentage(completedStepCount, totalSteps)

    val progressFloat: Float
        get() = if (totalSteps > 0) completedStepCount.toFloat() / totalSteps.toFloat() else 0f
}
