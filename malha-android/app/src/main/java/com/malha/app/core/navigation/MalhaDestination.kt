package com.malha.app.core.navigation

sealed class MalhaDestination(
    val route: String,
    val label: String
) {
    data object Home : MalhaDestination("home", "Home")
    data object Projects : MalhaDestination("projects", "Projects")
    data object Patterns : MalhaDestination("patterns", "Patterns")
    data object Materials : MalhaDestination("materials", "Materials")
    data object Settings : MalhaDestination("settings", "Settings")
    data object ProjectExecution : MalhaDestination("project/{projectId}", "Project")
    data object PatternDetail : MalhaDestination("pattern/{patternId}", "Pattern")

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
