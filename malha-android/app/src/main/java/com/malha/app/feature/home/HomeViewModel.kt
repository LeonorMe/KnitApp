package com.malha.app.feature.home

import android.app.Application
import android.text.format.DateUtils
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.malha.app.core.app.appContainer
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class HomeViewModel(application: Application) : AndroidViewModel(application) {
    val uiState: StateFlow<HomeUiState> = combine(
        appContainer.projectRepository.observeActiveProjects(),
        appContainer.materialRepository.observeMaterials(),
        appContainer.preferencesRepository.userPreferences
    ) { projects, materials, prefs ->
        HomeUiState(
            isLoading = false,
            projects = projects,
            insights = HomeInsightEngine.buildInsights(
                projects = projects,
                materials = materials
            ),
            showDailyYarn = !DateUtils.isToday(prefs.lastDailyRewardTimestamp)
        )
    }
    .stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = HomeUiState()
    )

    init {
        viewModelScope.launch {
            appContainer.databaseSeeder.seedIfEmpty()
        }
    }

    fun claimDailyReward(amount: Int) {
        viewModelScope.launch {
            appContainer.socialRepository.addCoins(amount)
            appContainer.preferencesRepository.updateLastDailyReward(System.currentTimeMillis())
        }
    }
}
