package com.malha.app

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.List
import androidx.compose.material.icons.outlined.Build
import androidx.compose.material.icons.outlined.Face
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.malha.app.core.navigation.MalhaDestination
import com.malha.app.core.navigation.MalhaNavHost

@Composable
fun MalhaApp() {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    Scaffold(
        bottomBar = {
            NavigationBar {
                MalhaDestination.topLevel.forEach { destination ->
                    NavigationBarItem(
                        selected = currentRoute == destination.route,
                        onClick = {
                            navController.navigate(destination.route) {
                                popUpTo(navController.graph.startDestinationId) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = {
                            Icon(
                                imageVector = when (destination) {
                                    MalhaDestination.Home -> Icons.Outlined.Home
                                    MalhaDestination.Projects -> Icons.Outlined.Folder
                                    MalhaDestination.Patterns -> Icons.AutoMirrored.Outlined.List
                                    MalhaDestination.Materials -> Icons.Outlined.Build
                                    MalhaDestination.Aidi -> Icons.Outlined.Face
                                    MalhaDestination.Settings -> Icons.Outlined.Settings
                                    MalhaDestination.PatternDetail -> Icons.AutoMirrored.Outlined.List
                                    MalhaDestination.ProjectExecution -> Icons.Outlined.Folder
                                    MalhaDestination.ProfileEdit -> Icons.Outlined.Face
                                },
                                contentDescription = androidx.compose.ui.res.stringResource(destination.titleResId)
                            )
                        },
                        label = { Text(androidx.compose.ui.res.stringResource(destination.titleResId)) }
                    )
                }
            }
        }
    ) { innerPadding ->
        MalhaNavHost(
            navController = navController,
            modifier = Modifier.padding(innerPadding)
        )
    }
}
