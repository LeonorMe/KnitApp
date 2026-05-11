package com.malha.app.core.navigation

sealed class MalhaDestination(val route: String, val label: String) {
    object Home : MalhaDestination("home", "Home")
    object Projects : MalhaDestination("projects", "Projects")
    object Patterns : MalhaDestination("patterns", "Patterns")
    object Materials : MalhaDestination("materials", "Materials")
    object Settings : MalhaDestination("settings", "Settings")
    object PatternDetail : MalhaDestination("pattern_detail", "Pattern Detail")
    object ProjectExecution : MalhaDestination("project_execution", "Project Execution")

    companion object {
        val topLevel = listOf(Home, Projects, Patterns, Materials, Settings)
    }
}

// NavHost composable
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

@Composable
fun MalhaNavHost(navController: NavHostController, modifier: androidx.compose.ui.Modifier = androidx.compose.ui.Modifier) {
    NavHost(navController = navController, startDestination = MalhaDestination.Home.route, modifier = modifier) {
        composable(MalhaDestination.Home.route) { com.malha.app.feature.home.HomeScreen() }
        composable(MalhaDestination.Projects.route) { com.malha.app.feature.projects.ProjectsScreen() }
        composable(MalhaDestination.Patterns.route) { com.malha.app.feature.patterns.PatternsScreen() }
        composable(MalhaDestination.Materials.route) { com.malha.app.feature.materials.MaterialsScreen() }
        composable(MalhaDestination.Settings.route) { com.malha.app.feature.settings.SettingsScreen() }
        // Placeholder destinations for now
        composable(MalhaDestination.PatternDetail.route) { com.malha.app.PlaceholderScreen("Pattern Detail") }
        composable(MalhaDestination.ProjectExecution.route) { com.malha.app.PlaceholderScreen("Project Execution") }
    }
}
