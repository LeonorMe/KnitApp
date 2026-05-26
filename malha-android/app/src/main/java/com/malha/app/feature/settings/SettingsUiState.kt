package com.malha.app.feature.settings

import com.malha.app.core.firebase.AuthUser
import com.malha.app.core.preferences.UserPreferences

data class SettingsUiState(
    val user: AuthUser? = null,
    val preferences: UserPreferences = UserPreferences(),
    val isSigningIn: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null,
    val authMode: AuthMode? = null
) {
    val isSignedIn: Boolean
        get() = user != null
}

enum class AuthMode {
    Login,
    CreateAccount
}
