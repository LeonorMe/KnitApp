package com.malha.app.core.firebase

data class AuthUser(
    val id: String,
    val displayName: String?,
    val email: String?
)

interface AuthService {
    val currentUser: AuthUser?
    fun isSignedIn(): Boolean
    suspend fun signInWithGoogleIdToken(idToken: String): AuthUser
    fun signOut()
}
