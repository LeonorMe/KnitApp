package com.malha.app.core.firebase

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.tasks.await

class FirebaseAuthService(
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
) : AuthService {
    override val currentUser: AuthUser?
        get() = firebaseAuth.currentUser?.let { user ->
            AuthUser(
                id = user.uid,
                displayName = user.displayName,
                email = user.email
            )
        }

    override fun isSignedIn(): Boolean {
        return firebaseAuth.currentUser != null
    }

    override suspend fun signInWithGoogleIdToken(idToken: String): AuthUser {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        val result = firebaseAuth.signInWithCredential(credential).await()
        val user = requireNotNull(result.user) {
            "Google sign-in completed without a Firebase user."
        }

        return AuthUser(
            id = user.uid,
            displayName = user.displayName,
            email = user.email
        )
    }

    override fun signOut() {
        firebaseAuth.signOut()
    }
}
