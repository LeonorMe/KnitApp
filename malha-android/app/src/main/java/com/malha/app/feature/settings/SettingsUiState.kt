package com.malha.app.feature.settings

import com.malha.app.core.firebase.AuthUser

data class SettingsUiState(
    val user: AuthUser? = null,
    val isSigningIn: Boolean = false,
    val errorMessage: String? = null
) {
    val isSignedIn: Boolean
        get() = user != null
}

