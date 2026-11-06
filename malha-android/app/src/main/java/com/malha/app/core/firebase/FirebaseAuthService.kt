package com.malha.app.core.firebase

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class FirebaseAuthService(
    private val firebaseAuth: FirebaseAuth? = runCatching { FirebaseAuth.getInstance() }.getOrNull()
) : AuthService {

    override val currentUser: AuthUser?
        get() = firebaseAuth?.currentUser?.toAuthUser()

    override val authState: Flow<AuthUser?> = callbackFlow {
        val auth = firebaseAuth
        if (auth == null) {
            trySend(null)
            awaitClose { }
            return@callbackFlow
        }
        val listener = FirebaseAuth.AuthStateListener { a ->
            trySend(a.currentUser?.toAuthUser())
        }
        auth.addAuthStateListener(listener)
        awaitClose { auth.removeAuthStateListener(listener) }
    }

    override fun isSignedIn(): Boolean {
        return firebaseAuth?.currentUser != null
    }

    override suspend fun signInWithGoogleIdToken(idToken: String): AuthUser {
        val auth = checkNotNull(firebaseAuth) { "Firebase Auth is not available." }
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        val result = auth.signInWithCredential(credential).await()
        val user = requireNotNull(result.user) {
            "Google sign-in completed without a Firebase user."
        }

        return user.toAuthUser()
    }

    override suspend fun signInAnonymously(): AuthUser {
        val auth = checkNotNull(firebaseAuth) { "Firebase Auth is not available." }
        val result = auth.signInAnonymously().await()
        val user = requireNotNull(result.user) {
            "Anonymous sign-in completed without a Firebase user."
        }
        return user.toAuthUser()
    }

    override fun signOut() {
        firebaseAuth?.signOut()
    }

    private fun FirebaseUser.toAuthUser(): AuthUser {
        return AuthUser(
            id = uid,
            displayName = displayName,
            email = email,
            photoUrl = photoUrl?.toString(),
            isAnonymous = isAnonymous
        )
    }
}
