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
import android.net.Uri
import android.util.Log
import com.malha.app.core.util.ImageStorageHelper
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
            appContainer.socialRepository.updateProfile(
                name = username,
                bio = bio,
                profilePicUri = uiState.value.preferences.profilePhotoUri
            )
        }
    }

    fun updateProfilePhoto(localUri: Uri){
        viewModelScope.launch {
            val context = getApplication<Application>()
            val persistentUri = ImageStorageHelper.saveImageToInternalStorage(context, localUri)
                ?: localUri.toString()

            // 1. Immediately persist locally so it renders across all screens & restarts
            appContainer.preferencesRepository.updateProfilePhoto(persistentUri)
            _uiState.update {
                it.copy(preferences = it.preferences.copy(profilePhotoUri = persistentUri))
            }

            // 2. Safely sync to cloud if user is signed in
            val user = uiState.value.user
            if (user != null && !user.isAnonymous) {
                runCatching {
                    val downloadUrl = appContainer.cloudDataService.uploadProfilePhoto(
                        userId = user.id,
                        localUri = localUri
                    )
                    appContainer.preferencesRepository.updateProfilePhoto(downloadUrl)
                    val prefs = uiState.value.preferences
                    appContainer.socialRepository.updateProfile(
                        name = prefs.username.orEmpty(),
                        bio = prefs.bio,
                        profilePicUri = downloadUrl
                    )
                }.onFailure { e ->
                    Log.w("SettingsViewModel", "Failed to upload profile photo to cloud: ${e.message}")
                }
            }
        }
    }

    fun setPendingProfilePhoto(uri: String) {
        _uiState.update {
            it.copy(preferences = it.preferences.copy(profilePhotoUri = uri))
        }
    }

    fun updateReminder(
        enabled: Boolean,
        regularity: com.malha.app.core.preferences.ReminderRegularity,
        daysOfWeek: Set<Int>,
        hour: Int,
        minute: Int
    ) {
        viewModelScope.launch {
            appContainer.preferencesRepository.updateReminder(enabled, regularity, daysOfWeek, hour, minute)
            com.malha.app.core.notification.DailyReminderScheduler.setReminder(
                context = getApplication(),
                enabled = enabled,
                regularity = regularity,
                daysOfWeek = daysOfWeek,
                hour = hour,
                minute = minute
            )
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
                val currentPrefs = uiState.value.preferences
                val newUsername = currentPrefs.username ?: user.displayName
                val newPhoto = currentPrefs.profilePhotoUri ?: user.photoUrl
                appContainer.preferencesRepository.updateProfile(newUsername, currentPrefs.bio)
                if (newPhoto != null) {
                    appContainer.preferencesRepository.updateProfilePhoto(newPhoto)
                }

                _uiState.update {
                    it.copy(
                        user = user,
                        isSigningIn = false,
                        authMode = null,
                        errorMessage = null,
                        successMessage = when (mode) {
                            AuthMode.CreateAccount -> "Google account connected. Your profile is ready."
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
        viewModelScope.launch {
            appContainer.authService.signOut()
            appContainer.preferencesRepository.clearProfile()
            runCatching {
                appContainer.authService.signInAnonymously()
            }
            _uiState.update {
                SettingsUiState(preferences = it.preferences, successMessage = "Signed out.")
            }
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
                val cloudPhoto = profile?.get("profilePhotoUri") as? String
                if (!cloudName.isNullOrBlank() || !cloudBio.isNullOrBlank()) {
                    val currentPrefs = uiState.value.preferences
                    appContainer.preferencesRepository.updateProfile(
                        username = cloudName ?: currentPrefs.username,
                        bio = cloudBio ?: currentPrefs.bio
                    )
                }
                if (!cloudPhoto.isNullOrBlank()) {
                    appContainer.preferencesRepository.updateProfilePhoto(cloudPhoto)
                }
            }
        }
    }
}
