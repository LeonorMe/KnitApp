package com.malha.app.domain.adaptation

import com.malha.app.domain.model.Material

/**
 * Interface for adapting pattern specifications to user inventory materials.
 * Serves as the primary extension point for future AI-assisted pattern recalculations.
 */
interface PatternAdaptationEngine {
    fun adapt(
        requirement: PatternRequirement,
        selectedNeedle: Material?,
        selectedYarn: Material?
    ): AdaptationResult
}
