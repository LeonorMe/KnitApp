package com.malha.app.core.navigation

import androidx.annotation.StringRes
import com.malha.app.R

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

sealed class MalhaDestination(val route: String, @StringRes val titleResId: Int) {
    object Home : MalhaDestination("home", R.string.nav_home)
    object Projects : MalhaDestination("projects", R.string.nav_projects)
    object Patterns : MalhaDestination("patterns", R.string.nav_patterns)
    object Materials : MalhaDestination("materials", R.string.nav_materials)
    object Settings : MalhaDestination("settings", R.string.nav_settings)
    object PatternDetail : MalhaDestination("pattern_detail", R.string.title_patterns)
    object ProjectExecution : MalhaDestination("project_execution", R.string.title_projects)

    companion object {
        val topLevel = listOf(Home, Projects, Patterns, Materials, Settings)
    }
}

@Composable
fun MalhaNavHost(navController: NavHostController, modifier: androidx.compose.ui.Modifier = androidx.compose.ui.Modifier) {
    NavHost(
        navController = navController,
        startDestination = MalhaDestination.Home.route,
        modifier = modifier,
        enterTransition = {
            slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Left, animationSpec = tween(300))
        },
        exitTransition = {
            slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Left, animationSpec = tween(300))
        },
        popEnterTransition = {
            slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Right, animationSpec = tween(300))
        },
        popExitTransition = {
            slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Right, animationSpec = tween(300))
        }
    ) {
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
