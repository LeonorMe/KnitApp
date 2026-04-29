package com.malha.app.feature.patterns

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.malha.app.core.app.appContainer
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class PatternsViewModel(application: Application) : AndroidViewModel(application) {
    val uiState: StateFlow<PatternsUiState> = appContainer.patternRepository
        .observePatterns()
        .map { patterns -> PatternsUiState(isLoading = false, patterns = patterns) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = PatternsUiState()
        )

    init {
        viewModelScope.launch {
            appContainer.databaseSeeder.seedIfEmpty()
        }
    }
}

