package com.malha.app.feature.patterns

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.malha.app.core.app.appContainer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PatternsViewModel(application: Application) : AndroidViewModel(application) {
    private val creationState = MutableStateFlow(PatternsCreationState())

    val uiState: StateFlow<PatternsUiState> = appContainer.patternRepository
        .observePatterns()
        .combine(creationState) { patterns, creation ->
            PatternsUiState(
                isLoading = false,
                patterns = patterns,
                isCreating = creation.isCreating,
                errorMessage = creation.errorMessage
            )
        }
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

    fun createPattern(title: String, instructionsText: String) {
        val trimmedTitle = title.trim()
        val instructions = instructionsText
            .lineSequence()
            .map { it.trim() }
            .filter { it.isNotBlank() }
            .toList()

        when {
            trimmedTitle.isBlank() -> {
                creationState.update { it.copy(errorMessage = "Pattern title is required.") }
                return
            }
            instructions.isEmpty() -> {
                creationState.update { it.copy(errorMessage = "Add at least one instruction.") }
                return
            }
        }

        viewModelScope.launch {
            creationState.update { it.copy(isCreating = true, errorMessage = null) }
            runCatching {
                appContainer.patternRepository.createManualPattern(
                    title = trimmedTitle,
                    instructions = instructions
                )
            }.onFailure { error ->
                creationState.update {
                    it.copy(errorMessage = error.message ?: "Could not create pattern.")
                }
            }
            creationState.update { it.copy(isCreating = false) }
        }
    }

    fun clearError() {
        creationState.update { it.copy(errorMessage = null) }
    }
}

private data class PatternsCreationState(
    val isCreating: Boolean = false,
    val errorMessage: String? = null
)
