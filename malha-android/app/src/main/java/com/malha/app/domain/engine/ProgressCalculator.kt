package com.malha.app.domain.engine

import kotlin.math.floor

object ProgressCalculator {

    /**
     * Calculates project execution progress percentage using integer floor:
     * floor(100 * completedExpandedInstructions / totalExpandedInstructions).
     *
     * Invariant: Internal counter taps within an instruction do not change global percentage
     * until the instruction is completed.
     */
    fun calculatePercentage(completedCount: Int, totalCount: Int): Int {
        if (totalCount <= 0) return 0
        val clampedCompleted = completedCount.coerceIn(0, totalCount)
        return floor((clampedCompleted.toDouble() / totalCount.toDouble()) * 100.0).toInt()
    }
}
