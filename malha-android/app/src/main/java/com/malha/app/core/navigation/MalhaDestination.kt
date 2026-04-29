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

    companion object {
        val topLevel = listOf(Home, Projects, Patterns, Materials, Settings)
    }
}

