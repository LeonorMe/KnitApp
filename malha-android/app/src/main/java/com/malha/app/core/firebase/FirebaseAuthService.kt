package com.malha.app.core.firebase

import com.google.firebase.auth.FirebaseAuth

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

    override fun signOut() {
        firebaseAuth.signOut()
    }
}

