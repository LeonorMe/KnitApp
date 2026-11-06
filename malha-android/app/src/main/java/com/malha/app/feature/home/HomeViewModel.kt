package com.malha.app.feature.home

import android.app.Application
import android.text.format.DateUtils
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.malha.app.core.app.appContainer
import com.malha.app.core.util.SocialDefaults.INITIAL_COINS
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class HomeViewModel(application: Application) : AndroidViewModel(application) {
    val uiState: StateFlow<HomeUiState> = combine(
        appContainer.projectRepository.observeActiveProjects(),
        appContainer.materialRepository.observeMaterials(),
        appContainer.preferencesRepository.userPreferences,
        appContainer.socialRepository.observeCurrentUser()
    ) { projects, materials, prefs, currentUser ->
        HomeUiState(
            isLoading = false,
            projects = projects,
            aidiSuggestions = emptyList(),
            showDailyYarn = !DateUtils.isToday(prefs.lastDailyRewardTimestamp),
            showSquareChallenge = !DateUtils.isToday(prefs.lastSquareChallengeTimestamp),
            userName = (prefs.username ?: currentUser?.name ?: "Guest")
                .trim()
                .substringBefore(" "),
            profilePhotoUri = prefs.profilePhotoUri ?: currentUser?.profilePicUri,
            coinBalance = currentUser?.coins ?: INITIAL_COINS
        )
    }
    .stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = HomeUiState()
    )

    init {
        viewModelScope.launch {
            appContainer.databaseSeeder.seed()
        }
    }

    fun claimDailyReward(amount: Int) {
        viewModelScope.launch {
            appContainer.socialRepository.addCoins(amount)
            appContainer.preferencesRepository.updateLastDailyReward(System.currentTimeMillis())
        }
    }

    fun claimSquareReward(amount: Int) {
        viewModelScope.launch {
            appContainer.socialRepository.addCoins(amount)
            appContainer.preferencesRepository.updateLastSquareChallenge(System.currentTimeMillis())
        }
    }

    fun startSquareChallengeProject(onProjectReady: (String) -> Unit) {
        viewModelScope.launch {
            // Find existing challenge project or create a new one with square_purl_001
            val existing = appContainer.projectRepository.observeAllProjects().firstOrNull()
                ?.firstOrNull { it.patternId == "square_purl_001" }

            val projectId = if (existing != null) {
                existing.id
            } else {
                appContainer.projectRepository.createProject(
                    name = "Daily 10x10 Swatch",
                    patternId = "square_purl_001"
                )
            }
            // Mark project as active so it executes immediately
            appContainer.projectRepository.startProject(projectId)
            // Mark challenge as started/done for today so badge disappears
            appContainer.preferencesRepository.updateLastSquareChallenge(System.currentTimeMillis())
            onProjectReady(projectId)
        }
    }
}
