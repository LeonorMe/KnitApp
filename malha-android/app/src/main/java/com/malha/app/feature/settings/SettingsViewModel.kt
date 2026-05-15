package com.malha.app.feature.settings

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.malha.app.core.app.appContainer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

import kotlinx.coroutines.flow.collectLatest

class SettingsViewModel(application: Application) : AndroidViewModel(application) {
    private val _uiState = MutableStateFlow(
        SettingsUiState(user = appContainer.authService.currentUser)
    )
    val uiState: StateFlow<SettingsUiState> = _uiState

    init {
        viewModelScope.launch {
            appContainer.preferencesRepository.userPreferences.collectLatest { prefs ->
                _uiState.update { it.copy(preferences = prefs) }
            }
        }
    }

    fun updateTheme(theme: com.malha.app.core.preferences.AppTheme) {
        viewModelScope.launch { appContainer.preferencesRepository.updateTheme(theme) }
    }

    fun updateLanguage(language: com.malha.app.core.preferences.AppLanguage) {
        viewModelScope.launch { appContainer.preferencesRepository.updateLanguage(language) }
    }

    fun updateUnits(units: com.malha.app.core.preferences.AppUnits) {
        viewModelScope.launch { appContainer.preferencesRepository.updateUnits(units) }
    }

    fun updateProfile(username: String, bio: String) {
        viewModelScope.launch { 
            appContainer.preferencesRepository.updateProfile(username, bio)
            // Also sync to cloud if signed in
            uiState.value.user?.let { user ->
                // This would need more complex logic to update CloudDataService
            }
        }
    }

    fun signInWithGoogleToken(idToken: String?) {
        if (idToken.isNullOrBlank()) {
            _uiState.update {
                it.copy(errorMessage = "Google sign-in did not return an ID token.")
            }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isSigningIn = true, errorMessage = null) }

            runCatching {
                val user = appContainer.authService.signInWithGoogleIdToken(idToken)
                appContainer.cloudDataService.upsertUserProfile(user)
                user
            }.onSuccess { user ->
                _uiState.update {
                    it.copy(user = user, isSigningIn = false, errorMessage = null)
                }
            }.onFailure { error ->
                _uiState.update {
                    it.copy(
                        isSigningIn = false,
                        errorMessage = error.message ?: "Could not sign in with Google."
                    )
                }
            }
        }
    }

    fun signOut() {
        appContainer.authService.signOut()
        _uiState.update {
            SettingsUiState(user = null)
        }
    }

    fun showSignInError(message: String) {
        _uiState.update {
            it.copy(isSigningIn = false, errorMessage = message)
        }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}
