package com.malha.app.core.navigation

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import com.malha.app.feature.aidi.AidiScreen
import com.malha.app.feature.execution.ProjectExecutionScreen
import com.malha.app.feature.home.HomeScreen
import com.malha.app.feature.materials.MaterialsScreen
import com.malha.app.feature.patterns.PatternsScreen
import com.malha.app.feature.patterns.detail.PatternDetailScreen
import com.malha.app.feature.profile.ProfileEditScreen
import com.malha.app.feature.projects.ProjectSettingsScreen
import com.malha.app.feature.projects.ProjectsScreen
import com.malha.app.feature.settings.SettingsScreen
import com.malha.app.feature.settings.SettingsViewModel
import com.malha.app.feature.social.CommunityFeedScreen
import com.malha.app.feature.social.CreatePostScreen
import com.malha.app.feature.social.ProfileScreen

@Composable
fun MalhaNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    viewModel: SettingsViewModel = viewModel()
) {
    val profilePhotoPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            if (uri != null) {
                viewModel.setPendingProfilePhoto(uri.toString())
                viewModel.updateProfilePhoto(uri)
            }
        }
    )
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
                    navController.navigate(MalhaDestination.Settings.route)
                }
            )
        }

        composable(MalhaDestination.Projects.route) {
            ProjectsScreen(
                onOpenProjectExecution = { projectId ->
                    navController.navigate(MalhaDestination.projectExecutionRoute(projectId))
                },
                onOpenProjectSettings = { projectId ->
                    navController.navigate(MalhaDestination.projectSettingsRoute(projectId))
                }
            )
        }

        composable(MalhaDestination.Patterns.route) {
            HomeScreen(
                onOpenProject = { projectId ->
                    // Home shortcut keeps "jump into execution" semantics
                    navController.navigate(MalhaDestination.projectExecutionRoute(projectId))
                },
                onNavigateToProfile = {
                    navController.navigate(MalhaDestination.Settings.route)
                }
            )
        }

        composable(
            route = MalhaDestination.ProjectExecution.route,
            deepLinks = listOf(
                navDeepLink { uriPattern = "https://malha.app/project-template/{projectId}" }
            ),
            arguments = listOf(navArgument("projectId") { type = NavType.StringType })
        ) { entry ->
            val projectId = entry.arguments?.getString("projectId").orEmpty()
            ProjectExecutionScreen(
                projectId = projectId,
                onBack = { navController.popBackStack() },
                onOpenSettings = { pid ->
                    navController.navigate(MalhaDestination.projectSettingsRoute(pid))
                }
            )
        }

        composable(
            route = MalhaDestination.ProjectSettings.route,
            arguments = listOf(navArgument("projectId") { type = NavType.StringType })
        ) { entry ->
            val projectId = entry.arguments?.getString("projectId").orEmpty()
            ProjectSettingsScreen(
                projectId = projectId,
                onStartProject = { pid ->
                    // Replace Settings on the stack with Execution.
                    // After "Começar Projeto", pressing back must NOT return to the form.
                    navController.navigate(MalhaDestination.projectExecutionRoute(pid)) {
                        popUpTo(MalhaDestination.ProjectSettings.route) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(MalhaDestination.Materials.route) {
            MaterialsScreen(
                onNavigateToPattern = { patternId ->
                    navController.navigate(MalhaDestination.patternDetailRoute(patternId))
                }
            )
        }

        composable(MalhaDestination.Community.route) {
            CommunityFeedScreen(
                onCreatePost = {
                    navController.navigate(MalhaDestination.CreatePost.route)
                },
                onNavigateToPattern = { patternId ->
                    navController.navigate(MalhaDestination.patternDetailRoute(patternId))
                },
                onNavigateToProfile = {
                    navController.navigate(MalhaDestination.Settings.route)
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

        composable(MalhaDestination.ProfileEdit.route) {
            ProfileEditScreen(
                onNavigateBack = { navController.popBackStack() },
                onChangePhoto = {
                    profilePhotoPicker.launch(
                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                }
            )
        }

        composable(
            route = MalhaDestination.PatternDetail.route,
            deepLinks = listOf(
                navDeepLink {
                    uriPattern = "https://malha.app/pattern/{patternId}"
                }
            ),
            arguments = listOf(
                navArgument("patternId") { type = NavType.StringType }
            )
        ) { entry ->
            val patternId = entry.arguments?.getString("patternId").orEmpty()
            PatternDetailScreen(
                patternId = patternId,
                onProjectCreated = { projectId ->
                    navController.navigate(MalhaDestination.projectSettingsRoute(projectId))
                }
            )
        }
    }
}