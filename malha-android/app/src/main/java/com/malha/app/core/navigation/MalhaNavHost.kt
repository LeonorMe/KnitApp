package com.malha.app.core.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.malha.app.feature.home.HomeScreen
import com.malha.app.feature.materials.MaterialsScreen
import com.malha.app.feature.patterns.PatternsScreen
import com.malha.app.feature.projects.ProjectsScreen
import com.malha.app.feature.settings.SettingsScreen

@Composable
fun MalhaNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = MalhaDestination.Home.route,
        modifier = modifier
    ) {
        composable(MalhaDestination.Home.route) {
            HomeScreen()
        }
        composable(MalhaDestination.Projects.route) {
            ProjectsScreen()
        }
        composable(MalhaDestination.Patterns.route) {
            PatternsScreen()
        }
        composable(MalhaDestination.Materials.route) {
            MaterialsScreen()
        }
        composable(MalhaDestination.Settings.route) {
            SettingsScreen()
        }
    }
}

