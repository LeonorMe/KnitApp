package com.malha.app.core.firebase

import kotlinx.coroutines.flow.Flow

data class AuthUser(
    val id: String,
    val displayName: String?,
    val email: String?
)

interface AuthService {
    val currentUser: AuthUser?
    val authState: Flow<AuthUser?>
    fun isSignedIn(): Boolean
    suspend fun signInWithGoogleIdToken(idToken: String): AuthUser
    fun signOut()
}
