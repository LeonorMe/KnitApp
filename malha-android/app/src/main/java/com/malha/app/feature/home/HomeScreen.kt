package com.malha.app.feature.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.malha.app.R
import com.malha.app.core.design.component.DailySquareChallengeDialog
import com.malha.app.core.design.component.DailyYarnShakeDialog
import com.malha.app.core.design.component.ImagePlaceholder
import com.malha.app.core.preferences.AppTheme
import androidx.core.graphics.toColorInt
import com.malha.app.domain.model.Project
import kotlinx.coroutines.delay

@Composable
fun HomeScreen(
    onOpenProject: (String) -> Unit,
    onNavigateToProfile: () -> Unit,
    viewModel: HomeViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showShakeDialog by remember { mutableStateOf(false) }
    var showSquareDialog by remember { mutableStateOf(false) }
    var contentVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        contentVisible = true
    }

    Scaffold { padding ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            color = colorScheme.background
        ) {
            AnimatedVisibility(
                visible = contentVisible,
                enter = fadeIn(animationSpec = tween(360)) + slideInVertically(
                    animationSpec = tween(360),
                    initialOffsetY = { it / 14 }
                )
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    // 1. Scrollable Page Content
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 24.dp)
                    ) {
                        // Polished Top Bar
                        HomeTopBar(
                            profilePhotoUri = uiState.profilePhotoUri,
                            coinBalance = uiState.coinBalance,
                            onNavigateToProfile = onNavigateToProfile,
                            onNavigateToStore = onNavigateToProfile // TODO: Navigate to store
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(bottom = 32.dp),
                            verticalArrangement = Arrangement.spacedBy(24.dp)
                        ) {
                            item {
                                Text(
                                    text = stringResource(R.string.greeting, uiState.userName),
                                    style = MaterialTheme.typography.headlineMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = colorScheme.onBackground.copy(alpha = 0.9f)
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = stringResource(R.string.to_continue),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = colorScheme.onBackground.copy(alpha = 0.7f)
                                )
                            }

                            if (uiState.isLoading) {
                                item {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(32.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        CircularProgressIndicator()
                                    }
                                }
                            } else if (uiState.projects.isEmpty()) {
                                item {
                                    Text(
                                        text = stringResource(R.string.no_active_projects_create_to_start),
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = colorScheme.onSurfaceVariant,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 48.dp),
                                        textAlign = TextAlign.Center
                                    )
                                }
                            } else {
                                itemsIndexed(uiState.projects) { index, project ->
                                    val alignment = if (index % 2 == 0) Alignment.TopStart else Alignment.TopEnd
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .animateContentSize(),
                                        contentAlignment = alignment
                                    ) {
                                        ProjectCrateCard(
                                            project = project,
                                            onOpenProject = onOpenProject
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // 2. Static Overlay Daily Challenge Badges
                    Column(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(top = 80.dp, end = 20.dp), // Overlay static on the right below top bar
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // 10x10 Challenge Button
                        AnimatedVisibility(
                            visible = uiState.showSquareChallenge,
                            enter = scaleIn(animationSpec = tween(300)) + fadeIn(),
                            exit = scaleOut(animationSpec = tween(200)) + fadeOut()
                        ) {
                            val rotation = remember { Animatable(0f) }

                            LaunchedEffect(Unit) {
                                while (true) {
                                    delay(7560)

                                    rotation.animateTo(-8f, tween(60))
                                    rotation.animateTo(8f, tween(60))
                                    rotation.animateTo(-8f, tween(60))
                                    rotation.animateTo(8f, tween(60))
                                    rotation.animateTo(0f, tween(80))
                                }
                            }

                            val colorIcon = colorScheme.onTertiaryContainer
                            val colorBackground = colorScheme.tertiaryContainer

                            ChallengeBadgeButton(
                                modifier = Modifier.graphicsLayer {
                                    rotationZ = rotation.value
                                },
                                icon = {
                                    Canvas(modifier = Modifier.size(28.dp)) {
                                        val size = size.width
                                        val divisions = 4
                                        val step = size / divisions
                                        // Draw Grid Lines
                                        for (i in 1 until divisions) {
                                            drawLine(
                                                color = colorIcon,
                                                start = Offset(i * step, 0f),
                                                end = Offset(i * step, size),
                                                strokeWidth = 2.5f
                                            )
                                            drawLine(
                                                color = colorIcon,
                                                start = Offset(0f, i * step),
                                                end = Offset(size, i * step),
                                                strokeWidth = 2.5f
                                            )
                                        }
                                    }
                                },
                                label = stringResource(R.string.chalange_10x10),
                                badgeColor = colorBackground,
                                borderColor = colorIcon,
                                onClick = { showSquareDialog = true }
                            )
                        }

                        // Daily Yarn Shake Button
                        AnimatedVisibility(
                            visible = uiState.showDailyYarn,
                            enter = scaleIn(animationSpec = tween(300)) + fadeIn(),
                            exit = scaleOut(animationSpec = tween(200)) + fadeOut()
                        ) {
                            val rotation = remember { Animatable(0f) }

                            LaunchedEffect(Unit) {
                                while (true) {
                                    delay(2000)

                                    rotation.animateTo( -8f,tween(60))
                                    rotation.animateTo(targetValue = 8f, tween(60))
                                    rotation.animateTo(targetValue = -8f, animationSpec = tween(60))
                                    rotation.animateTo(targetValue = 8f, animationSpec = tween(60))
                                    rotation.animateTo(targetValue = 0f, animationSpec = tween(100))
                                }
                            }

                            val colorIcon = colorScheme.onSecondaryContainer
                            val colorBackground = colorScheme.secondaryContainer

                            ChallengeBadgeButton(
                                modifier = Modifier.graphicsLayer {
                                    rotationZ = rotation.value
                                },
                                icon = {
                                    Icon(
                                        painter = painterResource(id = R.drawable.ic_yarn),
                                        contentDescription = stringResource(R.string.icon_description_daily_yarn_shake),
                                        tint = colorIcon,
                                        modifier = Modifier.size(30.dp)
                                    )
                                },
                                label = stringResource(R.string.chalange_shake),
                                badgeColor = colorBackground,
                                borderColor = colorIcon,
                                onClick = { showShakeDialog = true }
                            )

                        }
                    }
                }
            }
        }
    }

    if (showShakeDialog) {
        DailyYarnShakeDialog(
            onDismiss = { showShakeDialog = false },
            onRewardClaimed = { amount ->
                viewModel.claimDailyReward(amount)
                showShakeDialog = false
            }
        )
    }

    if (showSquareDialog) {
        DailySquareChallengeDialog(
            onDismiss = { showSquareDialog = false },
            onRewardClaimed = { amount ->
                viewModel.claimSquareReward(amount)
                showSquareDialog = false
            }
        )
    }
}

@Composable
private fun HomeTopBar(
    profilePhotoUri: String?,
    coinBalance: Int,
    onNavigateToProfile: () -> Unit,
    onNavigateToStore: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.spacedBy(2.dp)
        ){
            // Left: App Logo/Title
            Icon(
                painter = painterResource(id = R.drawable.ic_aidi),
                contentDescription = stringResource(R.string.app_icon_description),
                tint = colorScheme.primary,
                modifier = Modifier.size(36.dp)
            )
            Text(
                text = stringResource(R.string.app_name),
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = colorScheme.primary
                )
            )
        }

        // Right: Coins + Profile
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Coins Indicator
            Surface(
                onClick = onNavigateToStore,
                shape = RoundedCornerShape(50),
                color = colorScheme.surface,
                //border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_coin),
                        contentDescription = "Coins",
                        tint = Color.Unspecified,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(1.dp))
                    Text(
                        text = "$coinBalance",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = colorScheme.secondary
                    )
                }
            }

            // Profile Picture Circle
            if (!profilePhotoUri.isNullOrEmpty()) {
                Surface(
                    onClick = onNavigateToProfile,
                    modifier = Modifier.size(40.dp),
                    shape = CircleShape,
                    color = colorScheme.surfaceVariant
                ) {
                    ImagePlaceholder(
                        label = "P",
                        imageUri = profilePhotoUri,
                        size = 40.dp
                    )
                }
            } else {
                IconButton(
                    onClick = onNavigateToProfile,
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Profile",
                        tint = colorScheme.primary,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun ProjectCrateCard(
    project: Project,
    onOpenProject: (String) -> Unit
) {
    val defaultAccent = colorScheme.primary

    val projectAccent = remember(project.colorHex, defaultAccent) {
        project.colorHex
            ?.let { hex ->
                runCatching {
                    Color(android.graphics.Color.parseColor(hex))
                }.getOrNull()
            }
            ?: defaultAccent
    }
    val onAccent = if (projectAccent.luminance() < 0.38f) Color.White else colorScheme.primary

    Box(
        modifier = Modifier
            .size(240.dp)
            .clickable { onOpenProject(project.id) },
        contentAlignment = Alignment.TopCenter
    ) {
        // Crate Vector background decoration
        Icon(
            painter = painterResource(id = R.drawable.ic_materials),
            contentDescription = null,
            tint = projectAccent,
            modifier = Modifier.fillMaxSize()
        )

        // Project detail front card, positioned inside the crate opening
        Card(
            modifier = Modifier
                .padding(top = 114.dp)
                .width(160.dp)
                .height(84.dp),
            colors = CardDefaults.cardColors(
                containerColor = colorScheme.surface
            ),
            shape = RoundedCornerShape(6.dp),
            border = BorderStroke(1.5.dp, projectAccent.copy(alpha = 0.72f)),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Project Thumbnail
                ImagePlaceholder(
                    label = project.name.take(1).uppercase(),
                    imageUri = project.imageUri,
                    size = 48.dp,
                    modifier = Modifier.clip(RoundedCornerShape(4.dp))
                )

                // Project Details
                Column(
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = project.name,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        color = colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "${project.progressPercent}%",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold,
                        color = projectAccent
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    LinearProgressIndicator(
                        progress = { project.progressPercent / 100f },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(4.dp)
                            .clip(RoundedCornerShape(2.dp)),
                        color = projectAccent,
                        trackColor = projectAccent.copy(alpha = 0.2f)
                    )
                }
            }
        }
        if (project.colorHex != null) {
            Surface(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = (-42).dp, y = 34.dp)
                    .size(28.dp),
                shape = CircleShape,
                color = projectAccent,
                border = BorderStroke(2.dp, colorScheme.surface),
                shadowElevation = 3.dp
            ) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                    Text(
                        text = project.name.take(1).uppercase(),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = onAccent
                    )
                }
            }
        }
    }
}

@Composable
private fun ChallengeBadgeButton(
    icon: @Composable () -> Unit,
    label: String,
    badgeColor: Color,
    borderColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Box(modifier = modifier) {
            Surface(
                onClick = onClick,
                modifier = Modifier.size(56.dp),
                shape = CircleShape,
                color = badgeColor,
                border = BorderStroke(2.dp, borderColor), // todo remove?
                shadowElevation = 4.dp
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    icon()
                }
            }
        }

        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = colorScheme.onBackground.copy(alpha = 0.7f)
        )
    }
}
