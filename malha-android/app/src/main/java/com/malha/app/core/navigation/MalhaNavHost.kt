package com.malha.app.core.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.malha.app.feature.execution.ProjectExecutionScreen
import com.malha.app.feature.home.HomeScreen
import com.malha.app.feature.materials.MaterialsScreen
import com.malha.app.feature.patterns.detail.PatternDetailScreen
import com.malha.app.feature.patterns.PatternsScreen
import com.malha.app.feature.projects.ProjectsScreen
import com.malha.app.feature.settings.SettingsScreen
import com.malha.app.feature.social.CommunityFeedScreen
import com.malha.app.feature.social.CreatePostScreen
import com.malha.app.feature.social.ProfileScreen
import com.malha.app.feature.aidi.AidiScreen

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
            HomeScreen(
                onOpenProject = { projectId ->
                    navController.navigate(MalhaDestination.projectExecutionRoute(projectId))
                },
                onNavigateToProfile = {
                    navController.navigate(MalhaDestination.Profile.route)
                }
            )
        }
        composable(MalhaDestination.Projects.route) {
            ProjectsScreen(
                onOpenProject = { projectId ->
                    navController.navigate(MalhaDestination.projectExecutionRoute(projectId))
                }
            )
        }
        composable(MalhaDestination.Patterns.route) {
            PatternsScreen(
                onOpenPattern = { patternId ->
                    navController.navigate(MalhaDestination.patternDetailRoute(patternId))
                }
            )
        }
        composable(MalhaDestination.Materials.route) {
            MaterialsScreen()
        }
        composable(MalhaDestination.Community.route) {
            CommunityFeedScreen(
                onCreatePost = {
                    navController.navigate(MalhaDestination.CreatePost.route)
                }
            )
        }
        composable(MalhaDestination.Aidi.route) {
            AidiScreen()
        }
        composable(MalhaDestination.Profile.route) {
            ProfileScreen()
        }
        composable(MalhaDestination.CreatePost.route) {
            CreatePostScreen(
                onBack = { navController.popBackStack() }
            )
        }
        composable(MalhaDestination.Settings.route) {
            SettingsScreen(
                onNavigateToProfileEdit = {
                    navController.navigate(MalhaDestination.ProfileEdit.route)
                }
            )
        }
        composable(
            route = MalhaDestination.ProjectExecution.route,
            arguments = listOf(
                navArgument("projectId") {
                    type = NavType.StringType
                }
            )
        ) { entry ->
            val projectId = entry.arguments?.getString("projectId").orEmpty()
            ProjectExecutionScreen(projectId = projectId)
        }
        composable(
            route = MalhaDestination.PatternDetail.route,
            arguments = listOf(
                navArgument("patternId") {
                    type = NavType.StringType
                }
            )
        ) { entry ->
            val patternId = entry.arguments?.getString("patternId").orEmpty()
            PatternDetailScreen(
                patternId = patternId,
                onProjectStarted = { projectId ->
                    navController.navigate(MalhaDestination.projectExecutionRoute(projectId))
                }
            )
        }
    }
}
