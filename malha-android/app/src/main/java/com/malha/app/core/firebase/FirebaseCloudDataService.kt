package com.malha.app.core.firebase

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.malha.app.domain.model.MaterialType
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import android.net.Uri
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import com.malha.app.core.util.SocialDefaults.INITIAL_COINS
import com.malha.app.domain.model.Post

class FirebaseCloudDataService(
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
        val docRef = firestore.collection(CloudCollections.USERS).document(user.id)
        val doc = runCatching { docRef.get().await() }.getOrNull()

        val profile = mutableMapOf<String, Any?>(
            "id" to user.id,
            "email" to user.email,
            "updatedAt" to System.currentTimeMillis()
        )

        val existingName = doc?.getString("displayName")
        if (existingName.isNullOrBlank()) {
            profile["displayName"] = user.displayName
        }

        val existingPhoto = doc?.getString("profilePhotoUri")
        if (existingPhoto.isNullOrBlank()) {
            profile["profilePhotoUri"] = user.photoUrl
        }

        // Seed coins only if missing — don't clobber existing balance
        if (doc == null || !doc.contains("coins")) {
            profile["coins"] = INITIAL_COINS.toLong()
        }

        docRef.set(profile, SetOptions.merge()).await()
    }

    override suspend fun updateUserProfile(
        userId: String,
        username: String?,
        bio: String?,
        profilePhotoUri: String?,
        coins: Int
    ) {
        val profile = mutableMapOf<String, Any?>(
            "updatedAt" to System.currentTimeMillis(),
            "displayName" to username,
            "bio" to bio,
            "profilePhotoUri" to profilePhotoUri,
            "coins" to coins
        )

        firestore
            .collection(CloudCollections.USERS)
            .document(userId)
            .set(profile, SetOptions.merge())
            .await()
    }

    private val storage = FirebaseStorage.getInstance()

    override suspend fun uploadProfilePhoto(
        userId: String,
        localUri: Uri
    ): String {
        val ref = storage.reference
            .child("profile_photos/$userId.jpg")

        ref.putFile(localUri).await()

        return ref.downloadUrl.await().toString()
    }

    override suspend fun updateUserCoins(userId: String, delta: Int) {
        firestore.collection(CloudCollections.USERS).document(userId)
            .set(
                mapOf(
                    "coins" to FieldValue.increment(delta.toLong()),
                    "updatedAt" to System.currentTimeMillis()
                ),
                SetOptions.merge()
            )
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

    override suspend fun updateProjectColor(userId: String, projectId: String, colorHex: String?) {
        firestore
            .collection(CloudCollections.USERS)
            .document(userId)
            .collection(CloudCollections.PROJECTS)
            .document(projectId)
            .set(
                mapOf(
                    "colorHex" to colorHex,
                    "updatedAt" to System.currentTimeMillis()
                ),
                SetOptions.merge()
            )
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

    override suspend fun createPost(
        userId: String,
        userName: String,
        userProfilePic: String?,
        postId: String,
        patternId: String?,
        patternName: String?,
        localImageUri: String,
        description: String,
        status: String,
        createdAt: Long
    ): String {
        val imageUrl = uploadPostPhoto(
            postId = postId,
            localUri = Uri.parse(localImageUri)
        )

        val post = mapOf(
            "id" to postId,
            "userId" to userId,
            "userName" to userName,
            "userProfilePic" to userProfilePic,
            "patternId" to patternId,
            "patternName" to patternName,
            "imageUrl" to imageUrl,
            "description" to description,
            "status" to status,
            "createdAt" to createdAt,
            "updatedAt" to System.currentTimeMillis()
        )

        firestore.collection(CloudCollections.POSTS)
            .document(postId)
            .set(post)
            .await()

        return imageUrl
    }

    private suspend fun uploadPostPhoto(
        postId: String,
        localUri: Uri          // ✅ real Uri
    ): String {
        val ref = storage.reference.child("post_photos/$postId.jpg")
        ref.putFile(localUri).await()
        return ref.downloadUrl.await().toString()
    }

    override fun observePosts(): Flow<List<Post>> = callbackFlow {
        val registration = firestore
            .collection(CloudCollections.POSTS)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) { close(error); return@addSnapshotListener }
                val posts = snapshot?.documents.orEmpty().mapNotNull { doc ->
                    runCatching {
                        Post(
                            id             = doc.getString("id") ?: doc.id,
                            userId         = doc.getString("userId").orEmpty(),
                            userName       = doc.getString("userName").orEmpty(),
                            userProfilePic = doc.getString("userProfilePic"),
                            patternId      = doc.getString("patternId"),
                            patternName    = doc.getString("patternName"),
                            imageUri       = doc.getString("imageUrl").orEmpty(),
                            description    = doc.getString("description").orEmpty(),
                            status         = doc.getString("status") ?: "ongoing",
                            createdAt      = doc.getLong("createdAt") ?: 0L
                        )
                    }.getOrNull()
                }
                trySend(posts)
            }
        awaitClose { registration.remove() }
    }
    // Change uploadPostPhoto to take a Uri (consistent with uploadProfilePhoto)

}

