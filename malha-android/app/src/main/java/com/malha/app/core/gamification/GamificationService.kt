package com.malha.app.core.gamification

import com.malha.app.domain.model.PatternStep

class GamificationService {
    /**
     * Calculate craft coins earned for completing a step.
     * This aligns with the "Reward creation, not consumption" philosophy.
     */
    fun calculateCoins(step: PatternStep): Int {
        val base = 10
        val complexityBonus = when {
            step.type.name.contains("REPEAT") -> 5
            step.type.name.contains("JOIN") -> 15
            (step.stitchCountData?.toIntOrNull() ?: 0) > 100 -> 10
            else -> 0
        }
        return base + complexityBonus
    }
}
