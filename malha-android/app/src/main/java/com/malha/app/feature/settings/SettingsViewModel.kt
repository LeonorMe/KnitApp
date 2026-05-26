package com.malha.app.feature.settings

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.malha.app.core.app.appContainer
import kotlinx.coroutines.Job
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
    private var cloudProfileJob: Job? = null

    init {
        viewModelScope.launch {
            appContainer.preferencesRepository.userPreferences.collectLatest { prefs ->
                _uiState.update { it.copy(preferences = prefs) }
            }
        }
        viewModelScope.launch {
            appContainer.authService.authState.collectLatest { user ->
                _uiState.update { it.copy(user = user) }
                observeCloudProfile(user?.id)
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

    fun updateTextSize(multiplier: Float) {
        viewModelScope.launch { appContainer.preferencesRepository.updateTextSize(multiplier) }
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

    fun beginAuth(mode: AuthMode) {
        _uiState.update {
            it.copy(
                authMode = mode,
                isSigningIn = true,
                errorMessage = null,
                successMessage = null
            )
        }
    }

    fun signInWithGoogleToken(idToken: String?, mode: AuthMode = uiState.value.authMode ?: AuthMode.Login) {
        if (idToken.isNullOrBlank()) {
            _uiState.update {
                it.copy(
                    isSigningIn = false,
                    authMode = null,
                    errorMessage = "Google sign-in did not return an ID token."
                )
            }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isSigningIn = true, authMode = mode, errorMessage = null, successMessage = null) }

            runCatching {
                val user = appContainer.authService.signInWithGoogleIdToken(idToken)
                appContainer.cloudDataService.upsertUserProfile(user)
                user
            }.onSuccess { user ->
                _uiState.update {
                    it.copy(
                        user = user,
                        isSigningIn = false,
                        authMode = null,
                        errorMessage = null,
                        successMessage = when (mode) {
                            AuthMode.CreateAccount -> "Google account connected. Your Malha profile is ready."
                            AuthMode.Login -> "Signed in with Google."
                        }
                    )
                }
            }.onFailure { error ->
                _uiState.update {
                    it.copy(
                        isSigningIn = false,
                        authMode = null,
                        errorMessage = error.message ?: "Could not sign in with Google."
                    )
                }
            }
        }
    }

    fun signOut() {
        appContainer.authService.signOut()
        _uiState.update {
            SettingsUiState(preferences = it.preferences, successMessage = "Signed out.")
        }
    }

    fun showSignInError(message: String) {
        _uiState.update {
            it.copy(isSigningIn = false, authMode = null, errorMessage = message)
        }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    fun clearMessages() {
        _uiState.update { it.copy(errorMessage = null, successMessage = null) }
    }

    private fun observeCloudProfile(userId: String?) {
        cloudProfileJob?.cancel()
        if (userId == null) return

        cloudProfileJob = viewModelScope.launch {
            appContainer.cloudDataService.observeUserProfile(userId).collectLatest { profile ->
                val cloudName = profile?.get("displayName") as? String
                val cloudBio = profile?.get("bio") as? String
                if (!cloudName.isNullOrBlank() || !cloudBio.isNullOrBlank()) {
                    val currentPrefs = uiState.value.preferences
                    appContainer.preferencesRepository.updateProfile(
                        username = cloudName ?: currentPrefs.username,
                        bio = cloudBio ?: currentPrefs.bio
                    )
                }
            }
        }
    }
}
