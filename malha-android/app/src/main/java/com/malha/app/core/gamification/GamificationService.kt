package com.malha.app.core.gamification

import com.malha.app.domain.model.Project

class GamificationService {
    /**
     * Calculate craft coins earned for completing a project.
     * This aligns with the "Reward creation, not consumption" philosophy.
     * The reward for completing a knit or crochet project should be 10 coins superior
     * to the price of doing the project itself in terms of materials.
     * It should also account for the estimated number of hours of work dedicated to the project.
     * It should have a small account for the different sizes the project can have
     * The final number of coins should pay completely the last project to allow
     * the user to have the number of coins necessaries to repeat the project
     * with an extra if the user decides to try a new more expensive and complex project.
     */
    fun calculateCoins(project: Project): Int {
        val base = 10 // maybe 5?

        // Size Bonus
        val sizeBonus = when {
            project.selectedSize.toString().contains("S") -> 1
            project.selectedSize.toString().contains("M") -> 2
            project.selectedSize.toString().contains("L") -> 3
            else -> 0
        }

        // Material compensation: yarn and needles
        // dependes on how much wool was used and its price,
        // a dependes on the needles ans its price
        /*val materialsCompensation = when {
            project.component1().contains("") -> 2
            else -> 0
        }*/

        // Time compensation

        // Complexity bonus
        /*val complexityBonus = when {
            //project.level -> 1
            project.type.name.contains("JOIN") -> 15
            (project.stitchCountData?.toIntOrNull() ?: 0) > 100 -> 10
            else -> 0
        }*/
        return base + sizeBonus //+ complexityBonus
    }
}
