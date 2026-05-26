package com.malha.app.feature.home

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.malha.app.core.design.component.DailyYarnShakeDialog
import com.malha.app.core.design.component.ImagePlaceholder

@Composable
fun HomeScreen(
    onOpenProject: (String) -> Unit,
    onNavigateToProfile: () -> Unit,
    viewModel: HomeViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showShakeDialog by remember { mutableStateOf(false) }
    var contentVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        contentVisible = true
    }

    Scaffold(
        floatingActionButton = {
            AnimatedVisibility(
                visible = uiState.showDailyYarn,
                enter = scaleIn(animationSpec = tween(260)) + fadeIn(animationSpec = tween(260)),
                exit = scaleOut(animationSpec = tween(180)) + fadeOut(animationSpec = tween(180))
            ) {
                FloatingActionButton(
                    onClick = { showShakeDialog = true },
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                    contentColor = MaterialTheme.colorScheme.onTertiaryContainer
                ) {
                    Icon(Icons.Default.AutoAwesome, contentDescription = "Daily Yarn")
                }
            }
        }
    ) { padding ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            color = MaterialTheme.colorScheme.background
        ) {
            AnimatedVisibility(
                visible = contentVisible,
                enter = fadeIn(animationSpec = tween(360)) + slideInVertically(
                    animationSpec = tween(360),
                    initialOffsetY = { it / 14 }
                )
            ) {
                LazyColumn(
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 28.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    item {
                        HomeHeader(
                            userName = uiState.userName,
                            coinBalance = uiState.coinBalance,
                            onNavigateToProfile = onNavigateToProfile
                        )
                    }

                    item { SectionTitle("A continuar") }

                    item {
                        AnimatedContent(
                            targetState = HomeProjectContentState(
                                isLoading = uiState.isLoading,
                                isEmpty = uiState.projects.isEmpty()
                            ),
                            label = "home-projects-content"
                        ) { state ->
                            when {
                                state.isLoading -> InfoText("Loading projects...")
                                state.isEmpty -> InfoText("No active projects yet.")
                                else -> LazyRow(
                                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                                    contentPadding = PaddingValues(horizontal = 4.dp)
                                ) {
                                    items(uiState.projects.take(3)) { project ->
                                        val animatedProgress by animateFloatAsState(
                                            targetValue = project.progressPercent / 100f,
                                            animationSpec = tween(durationMillis = 700),
                                            label = "home-project-progress"
                                        )
                                        Card(
                                            colors = CardDefaults.cardColors(
                                                containerColor = MaterialTheme.colorScheme.surface
                                            ),
                                            modifier = Modifier
                                                .width(280.dp)
                                                .animateContentSize()
                                                .clickable { onOpenProject(project.id) }
                                        ) {
                                            Column(
                                                modifier = Modifier.padding(16.dp),
                                                verticalArrangement = Arrangement.spacedBy(10.dp)
                                            ) {
                                                ImagePlaceholder(label = project.name, imageUri = project.imageUri)
                                                Text(
                                                    text = project.name,
                                                    style = MaterialTheme.typography.titleLarge,
                                                    color = MaterialTheme.colorScheme.onSurface
                                                )
                                                Text(
                                                    text = "Progress: ${project.progressPercent}%",
                                                    style = MaterialTheme.typography.bodyLarge,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                                LinearProgressIndicator(
                                                    progress = { animatedProgress },
                                                    modifier = Modifier.fillMaxWidth()
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    item { SectionTitle("Atividade diaria") }

                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            DailyActionCard(
                                title = "Desenrolar",
                                subtitle = "Ganha moedas!",
                                container = MaterialTheme.colorScheme.tertiaryContainer,
                                content = MaterialTheme.colorScheme.onTertiaryContainer,
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { showShakeDialog = true }
                            )
                            DailyActionCard(
                                title = "Desafio 10x10",
                                subtitle = "+3 Moedas",
                                container = MaterialTheme.colorScheme.secondaryContainer,
                                content = MaterialTheme.colorScheme.onSecondaryContainer,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    if (uiState.aidiSuggestions.isNotEmpty()) {
                        item { SectionTitle("A Aidi sugere") }

                        items(uiState.aidiSuggestions) { suggestion ->
                            Card(
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .animateContentSize()
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Text(
                                        text = suggestion.title,
                                        style = MaterialTheme.typography.titleMedium,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = suggestion.message,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Button(
                                        onClick = { /* Navigate to Aidi detail */ },
                                        modifier = Modifier.align(Alignment.End)
                                    ) {
                                        Text("Ver detalhes")
                                    }
                                }
                            }
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.height(24.dp))
                        Text(
                            text = uiState.weeklySummary,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(16.dp))
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
}

@Composable
private fun HomeHeader(
    userName: String,
    coinBalance: Int,
    onNavigateToProfile: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = "Boa noite, $userName",
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = "$coinBalance Moedas",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }
        IconButton(onClick = onNavigateToProfile) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = "Profile",
                modifier = Modifier.size(32.dp)
            )
        }
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleLarge,
        color = MaterialTheme.colorScheme.onBackground
    )
}

@Composable
private fun InfoText(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodyLarge,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
}

@Composable
private fun DailyActionCard(
    title: String,
    subtitle: String,
    container: androidx.compose.ui.graphics.Color,
    content: androidx.compose.ui.graphics.Color,
    modifier: Modifier = Modifier
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = container),
        modifier = modifier.animateContentSize()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = content)
            Spacer(modifier = Modifier.height(8.dp))
            Text(title, style = MaterialTheme.typography.titleMedium, color = content)
            Text(subtitle, style = MaterialTheme.typography.bodyMedium, color = content)
        }
    }
}

private data class HomeProjectContentState(
    val isLoading: Boolean,
    val isEmpty: Boolean
)
