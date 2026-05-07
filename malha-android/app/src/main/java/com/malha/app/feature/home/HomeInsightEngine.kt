package com.malha.app.feature.home

import com.malha.app.domain.model.Material
import com.malha.app.domain.model.Project
import kotlin.math.roundToInt

object HomeInsightEngine {
    private const val pausedProjectWindowMs = 7L * 24L * 60L * 60L * 1000L

    fun buildInsights(
        projects: List<Project>,
        materials: List<Material>,
        now: Long = System.currentTimeMillis()
    ): List<HomeInsight> {
        if (projects.isEmpty()) {
            return listOf(
                HomeInsight(
                    title = "Start your first project",
                    message = "Aidi can help you begin with one included starter pattern."
                )
            )
        }

        val insights = mutableListOf<HomeInsight>()
        val nearFinish = projects.maxByOrNull { it.progressPercent }
        if (nearFinish != null && nearFinish.progressPercent >= 75) {
            insights += HomeInsight(
                title = "Almost finished",
                message = "\"${nearFinish.name}\" is ${nearFinish.progressPercent}% done. A short session could finish it."
            )
        }

        val pausedProject = projects.maxByOrNull { now - it.updatedAt }
        if (pausedProject != null && now - pausedProject.updatedAt >= pausedProjectWindowMs) {
            val daysPaused = ((now - pausedProject.updatedAt).toDouble() / (24 * 60 * 60 * 1000))
                .roundToInt()
            insights += HomeInsight(
                title = "Project paused",
                message = "\"${pausedProject.name}\" has not been updated in about $daysPaused days."
            )
        }

        if (materials.isNotEmpty()) {
            insights += HomeInsight(
                title = "Stash available",
                message = "You already have ${materials.size} material items in local stash."
            )
        } else {
            insights += HomeInsight(
                title = "Add your first yarn",
                message = "Track one yarn entry to keep project planning realistic."
            )
        }

        val zeroProgressProjects = projects.count { it.progressPercent == 0 }
        if (zeroProgressProjects > 0) {
            insights += HomeInsight(
                title = "Get momentum",
                message = "$zeroProgressProjects project(s) have not started yet. Completing step 1 unlocks progress."
            )
        }

        if (insights.isEmpty()) {
            insights += HomeInsight(
                title = "Steady progress",
                message = "Aidi is keeping your project state organized locally."
            )
        }

        return insights.take(3)
    }
}
