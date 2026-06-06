package com.malha.app.core.firebase

import kotlinx.coroutines.flow.Flow

data class AuthUser(
    val id: String,
    val displayName: String?,
    val email: String?,
    val photoUrl: String? = null,
    val isAnonymous: Boolean = false
)

interface AuthService {
    val currentUser: AuthUser?
    val authState: Flow<AuthUser?>
    fun isSignedIn(): Boolean
    suspend fun signInWithGoogleIdToken(idToken: String): AuthUser
    suspend fun signInAnonymously(): AuthUser
    fun signOut()
}
