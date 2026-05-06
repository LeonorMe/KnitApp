package com.malha.app.feature.execution

import com.malha.app.domain.model.Pattern
import com.malha.app.domain.model.PatternStep
import com.malha.app.domain.model.Project

data class ProjectExecutionUiState(
    val isLoading: Boolean = true,
    val project: Project? = null,
    val pattern: Pattern? = null,
    val errorMessage: String? = null
) {
    val currentStep: PatternStep?
        get() {
            val steps = pattern?.steps.orEmpty()
            val index = project?.currentStepIndex ?: 0
            return steps.getOrNull(index.coerceIn(0, (steps.size - 1).coerceAtLeast(0)))
        }

    val currentStepNumber: Int
        get() = (project?.currentStepIndex ?: 0) + 1

    val totalSteps: Int
        get() = pattern?.steps?.size ?: 0

    val canGoPrevious: Boolean
        get() = (project?.currentStepIndex ?: 0) > 0

    val canGoNext: Boolean
        get() = totalSteps > 0 && (project?.currentStepIndex ?: 0) < totalSteps - 1
}

