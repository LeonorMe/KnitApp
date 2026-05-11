package com.malha.app.core.navigation

import androidx.annotation.StringRes
import com.malha.app.R

sealed class MalhaDestination(
    val route: String,
    @StringRes val titleResId: Int
) {
    data object Home : MalhaDestination("home", R.string.nav_home)
    data object Projects : MalhaDestination("projects", R.string.nav_projects)
    data object Patterns : MalhaDestination("patterns", R.string.nav_patterns)
    data object Materials : MalhaDestination("materials", R.string.nav_materials)
    data object Settings : MalhaDestination("settings", R.string.nav_settings)
    data object ProjectExecution : MalhaDestination("project/{projectId}", R.string.title_projects)
    data object PatternDetail : MalhaDestination("pattern/{patternId}", R.string.title_patterns)

    companion object {
        val topLevel = listOf(Home, Projects, Patterns, Materials, Settings)

        fun projectExecutionRoute(projectId: String): String {
            return "project/$projectId"
        }

        fun patternDetailRoute(patternId: String): String {
            return "pattern/$patternId"
        }
    }
}
