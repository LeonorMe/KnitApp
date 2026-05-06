package com.malha.app.core.firebase

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class FirestoreCloudDataService(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) : CloudDataService {
    override fun observeUserProfile(userId: String): Flow<Map<String, Any?>?> {
        return callbackFlow {
            val registration = firestore
                .collection(CloudCollections.USERS)
                .document(userId)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        close(error)
                        return@addSnapshotListener
                    }
                    trySend(snapshot?.data)
                }

            awaitClose { registration.remove() }
        }
    }

    override suspend fun upsertUserProfile(user: AuthUser) {
        val profile = mapOf(
            "id" to user.id,
            "displayName" to user.displayName,
            "email" to user.email,
            "updatedAt" to System.currentTimeMillis()
        )

        firestore
            .collection(CloudCollections.USERS)
            .document(user.id)
            .set(profile)
            .await()
    }
}

