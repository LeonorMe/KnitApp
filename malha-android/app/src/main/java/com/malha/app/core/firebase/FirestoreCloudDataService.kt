package com.malha.app.core.firebase

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.malha.app.domain.model.MaterialType
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
            "profilePhotoUri" to user.photoUrl,
            "updatedAt" to System.currentTimeMillis()
        )

        firestore
            .collection(CloudCollections.USERS)
            .document(user.id)
            .set(profile, SetOptions.merge())
            .await()
    }

    override suspend fun updateUserProfile(
        userId: String,
        username: String?,
        bio: String?,
        profilePhotoUri: String?
    ) {
        val profile = mutableMapOf<String, Any?>(
            "updatedAt" to System.currentTimeMillis()
        )
        profile["displayName"] = username
        profile["bio"] = bio
        profile["profilePhotoUri"] = profilePhotoUri

        firestore
            .collection(CloudCollections.USERS)
            .document(userId)
            .set(profile, SetOptions.merge())
            .await()
    }

    override suspend fun upsertProject(
        userId: String,
        projectId: String,
        name: String,
        patternId: String?,
        currentStepIndex: Int,
        progressPercent: Int,
        status: String
    ) {
        val project = mutableMapOf<String, Any?>(
            "id" to projectId,
            "userId" to userId,
            "currentStepIndex" to currentStepIndex,
            "progressPercent" to progressPercent,
            "status" to status,
            "updatedAt" to System.currentTimeMillis()
        )
        if (name.isNotBlank()) project["name"] = name
        if (patternId != null) project["patternId"] = patternId

        firestore
            .collection(CloudCollections.USERS)
            .document(userId)
            .collection(CloudCollections.PROJECTS)
            .document(projectId)
            .set(project, SetOptions.merge())
            .await()
    }

    override suspend fun upsertMaterial(
        userId: String,
        materialId: String,
        name: String,
        type: MaterialType,
        quantity: Double,
        unit: String,
        imageUri: String?
    ) {
        val material = mapOf(
            "id" to materialId,
            "userId" to userId,
            "name" to name,
            "type" to type.storageValue,
            "quantity" to quantity,
            "unit" to unit,
            "imageUri" to imageUri,
            "updatedAt" to System.currentTimeMillis()
        )

        firestore
            .collection(CloudCollections.USERS)
            .document(userId)
            .collection(CloudCollections.MATERIALS)
            .document(materialId)
            .set(material, SetOptions.merge())
            .await()
    }
}
