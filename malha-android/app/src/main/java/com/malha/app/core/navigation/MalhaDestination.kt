package com.malha.app.core.navigation

import androidx.annotation.StringRes
import com.malha.app.R

sealed class MalhaDestination(
    val route: String,
    @StringRes val titleResId: Int
) {
    data object Home : MalhaDestination("home", R.string.nav_home)
    data object Projects : MalhaDestination("projects", R.string.nav_projects)
    data object Aidi : MalhaDestination("aidi", R.string.nav_aidi)
    data object Materials : MalhaDestination("materials", R.string.nav_materials)
    data object Community : MalhaDestination("community", R.string.nav_community)
    
    // Hidden from bottom bar but accessible
    data object Patterns : MalhaDestination("patterns", R.string.nav_patterns)
    data object Profile : MalhaDestination("profile", R.string.nav_profile)
    data object Settings : MalhaDestination("settings", R.string.nav_settings)
    
    // Sub-screens
    data object ProjectExecution : MalhaDestination("project/{projectId}", R.string.title_projects)
    data object PatternDetail : MalhaDestination("pattern/{patternId}", R.string.title_patterns)
    data object CreatePost : MalhaDestination("create_post", R.string.nav_community)
    data object ProfileEdit : MalhaDestination("profile_edit", R.string.title_settings)

    companion object {
        val topLevel = listOf(Home, Projects, Aidi, Materials, Community)

        fun projectExecutionRoute(projectId: String): String {
            return "project/$projectId"
        }

        fun patternDetailRoute(patternId: String): String {
            return "pattern/$patternId"
        }
    }
}
