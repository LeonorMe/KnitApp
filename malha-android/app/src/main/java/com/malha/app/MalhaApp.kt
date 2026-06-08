package com.malha.app

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.List
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Build
import androidx.compose.material.icons.outlined.Face
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.vectorResource
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.malha.app.core.navigation.MalhaDestination
import com.malha.app.core.navigation.MalhaNavHost
import com.malha.app.R

@Composable
fun MalhaApp() {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    val isHomeHierarchy = currentRoute == "home" || 
                          currentRoute == "profile" || 
                          currentRoute == "settings" || 
                          currentRoute == "profile_edit" || 
                          currentRoute?.startsWith("project/") == true
                          
    val isCommunityHierarchy = currentRoute == "community" || 
                               currentRoute == "create_post" ||
                               currentRoute?.startsWith("pattern/") == true

    Scaffold(
        bottomBar = {
            NavigationBar {
                MalhaDestination.topLevel.forEach { destination ->
                    val isSelected = when (destination) {
                        MalhaDestination.Home -> isHomeHierarchy
                        MalhaDestination.Community -> isCommunityHierarchy
                        else -> currentRoute == destination.route
                    }

                    NavigationBarItem(
                        selected = isSelected,
                        onClick = {
                            if (destination == MalhaDestination.Home) {
                                // Clear all subpages of Home and return directly to root Home
                                navController.navigate(destination.route) {
                                    popUpTo(navController.graph.startDestinationId) {
                                        saveState = false
                                        inclusive = false
                                    }
                                    launchSingleTop = true
                                    restoreState = false
                                }
                            } else {
                                navController.navigate(destination.route) {
                                    popUpTo(navController.graph.startDestinationId) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        },
                        icon = {
                            Icon(
                                imageVector = when (destination) {
                                    MalhaDestination.Home -> androidx.compose.ui.graphics.vector.ImageVector.vectorResource(id = R.drawable.home_24px)
                                    MalhaDestination.Projects -> androidx.compose.ui.graphics.vector.ImageVector.vectorResource(id = R.drawable.ic_project)
                                    MalhaDestination.Patterns -> Icons.AutoMirrored.Outlined.List
                                    MalhaDestination.Materials -> androidx.compose.ui.graphics.vector.ImageVector.vectorResource(id = R.drawable.ic_materials)
                                    MalhaDestination.Community -> Icons.Outlined.Groups
                                    MalhaDestination.Aidi -> androidx.compose.ui.graphics.vector.ImageVector.vectorResource(id = R.drawable.ic_aidi)
                                    MalhaDestination.Profile -> Icons.Outlined.AccountCircle
                                    MalhaDestination.Settings -> Icons.Outlined.Settings
                                    else -> Icons.Outlined.Home
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
