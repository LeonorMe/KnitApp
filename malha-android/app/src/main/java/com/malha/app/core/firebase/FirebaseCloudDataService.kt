package com.malha.app.core.firebase

import android.util.Log
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
import androidx.core.net.toUri

private const val TAG = "FirebaseCloudData"

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
            localUri = localImageUri.toUri()
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
                if (error != null) {
                    Log.e(TAG, "observePosts failed: ${error.message}")
                    trySend(emptyList())
                    return@addSnapshotListener
                }
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

    override fun observeProjects(userId: String): Flow<List<Map<String, Any?>>> = callbackFlow {
        val registration = firestore
            .collection(CloudCollections.USERS)
            .document(userId)
            .collection(CloudCollections.PROJECTS)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e(TAG, "observeProjects failed: ${error.message}")
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                trySend(snapshot?.documents.orEmpty().mapNotNull { it.data })
            }
        awaitClose { registration.remove() }
    }

    override fun observeMaterials(userId: String): Flow<List<Map<String, Any?>>> = callbackFlow {
        val registration = firestore
            .collection(CloudCollections.USERS)
            .document(userId)
            .collection(CloudCollections.MATERIALS)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e(TAG, "observeMaterials failed: ${error.message}")
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                trySend(snapshot?.documents.orEmpty().mapNotNull { it.data })
            }
        awaitClose { registration.remove() }
    }

    override fun observePatterns(): Flow<List<Map<String, Any?>>> = callbackFlow {
        val registration = firestore
            .collection(CloudCollections.PATTERNS)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e(TAG, "observePatterns failed: ${error.message}")
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                trySend(snapshot?.documents.orEmpty().mapNotNull { it.data })
            }
        awaitClose { registration.remove() }
    }

    override fun observePatternSteps(): Flow<List<Map<String, Any?>>> = callbackFlow {
        val registration = firestore
            .collection(CloudCollections.PATTERN_STEPS)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e(TAG, "observePatternSteps failed: ${error.message}")
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                trySend(snapshot?.documents.orEmpty().mapNotNull { it.data })
            }
        awaitClose { registration.remove() }
    }

    override suspend fun upsertPattern(
        patternId: String,
        title: String,
        designer: String?,
        year: Int?,
        craft: String,
        difficulty: String,
        gaugeWidth: Int?,
        gaugeHeight: Int?,
        gaugeUnit: String?,
        sourceType: String,
        originalText: String?,
        verificationStatus: String,
        aiConfidence: Double,
        isPremium: Boolean,
        availableSizes: String?,
        selectedSize: String?,
        updatedAt: Long
    ) {
        val pattern = mapOf(
            "id" to patternId,
            "title" to title,
            "designer" to designer,
            "year" to year,
            "craft" to craft,
            "difficulty" to difficulty,
            "gaugeWidth" to gaugeWidth,
            "gaugeHeight" to gaugeHeight,
            "gaugeUnit" to gaugeUnit,
            "sourceType" to sourceType,
            "originalText" to originalText,
            "verificationStatus" to verificationStatus,
            "aiConfidence" to aiConfidence,
            "isPremium" to isPremium,
            "availableSizes" to availableSizes,
            "selectedSize" to selectedSize,
            "updatedAt" to updatedAt
        )
        firestore.collection(CloudCollections.PATTERNS).document(patternId).set(pattern, SetOptions.merge()).await()
    }

    override suspend fun upsertPatternStep(
        stepId: String,
        patternId: String,
        sectionId: String,
        orderIndex: Int,
        stepType: String,
        instruction: String,
        rowNumber: Int?,
        stitchCount: Int?,
        confidence: Double,
        repeatCount: Int?,
        everyNRows: Int?,
        startRow: Int?,
        endRow: Int?,
        condition: String?,
        stitchPatternId: String?,
        updatedAt: Long
    ) {
        val step = mapOf(
            "id" to stepId,
            "patternId" to patternId,
            "sectionId" to sectionId,
            "orderIndex" to orderIndex,
            "stepType" to stepType,
            "instruction" to instruction,
            "rowNumber" to rowNumber,
            "stitchCount" to stitchCount,
            "confidence" to confidence,
            "repeatCount" to repeatCount,
            "everyNRows" to everyNRows,
            "startRow" to startRow,
            "endRow" to endRow,
            "condition" to condition,
            "stitchPatternId" to stitchPatternId,
            "updatedAt" to updatedAt
        )
        firestore.collection(CloudCollections.PATTERN_STEPS).document(stepId).set(step, SetOptions.merge()).await()
    }

    override fun observeProjectMaterials(userId: String): Flow<List<Map<String, Any?>>> = callbackFlow {
        val registration = firestore
            .collection(CloudCollections.USERS)
            .document(userId)
            .collection(CloudCollections.PROJECT_MATERIALS)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e(TAG, "observeProjectMaterials failed: ${error.message}")
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                trySend(snapshot?.documents.orEmpty().mapNotNull { it.data })
            }
        awaitClose { registration.remove() }
    }

    override suspend fun upsertProjectMaterial(
        userId: String,
        id: String,
        projectId: String,
        materialId: String,
        plannedQuantity: Double?,
        usedQuantity: Double?
    ) {
        val pm = mapOf(
            "id" to id,
            "projectId" to projectId,
            "materialId" to materialId,
            "plannedQuantity" to plannedQuantity,
            "usedQuantity" to usedQuantity,
            "updatedAt" to System.currentTimeMillis()
        )
        firestore.collection(CloudCollections.USERS)
            .document(userId)
            .collection(CloudCollections.PROJECT_MATERIALS)
            .document(id)
            .set(pm, SetOptions.merge())
            .await()
    }

    override fun observeVariants(patternId: String): Flow<List<Map<String, Any?>>> = callbackFlow {
        val registration = firestore
            .collection(CloudCollections.VARIANTS)
            .whereEqualTo("patternId", patternId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e(TAG, "observeVariants failed: ${error.message}")
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                trySend(snapshot?.documents.orEmpty().mapNotNull { it.data })
            }
        awaitClose { registration.remove() }
    }

    override suspend fun upsertVariant(
        variantId: String,
        patternId: String,
        userId: String,
        name: String,
        notes: String?,
        updatedAt: Long
    ) {
        val variant = mapOf(
            "id" to variantId,
            "patternId" to patternId,
            "userId" to userId,
            "name" to name,
            "notes" to notes,
            "updatedAt" to updatedAt
        )
        firestore.collection(CloudCollections.VARIANTS).document(variantId).set(variant, SetOptions.merge()).await()
    }

    override fun observeReviews(patternId: String): Flow<List<Map<String, Any?>>> = callbackFlow {
        val registration = firestore
            .collection(CloudCollections.REVIEWS)
            .whereEqualTo("patternId", patternId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e(TAG, "observeReviews failed: ${error.message}")
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                trySend(snapshot?.documents.orEmpty().mapNotNull { it.data })
            }
        awaitClose { registration.remove() }
    }

    override suspend fun upsertReview(
        reviewId: String,
        patternId: String,
        userId: String,
        rating: Int,
        text: String,
        createdAt: Long
    ) {
        val review = mapOf(
            "id" to reviewId,
            "patternId" to patternId,
            "userId" to userId,
            "rating" to rating,
            "text" to text,
            "createdAt" to createdAt,
            "updatedAt" to System.currentTimeMillis()
        )
        firestore.collection(CloudCollections.REVIEWS).document(reviewId).set(review, SetOptions.merge()).await()
    }

    override fun observeAIRequests(userId: String): Flow<List<Map<String, Any?>>> = callbackFlow {
        val registration = firestore
            .collection(CloudCollections.AI_REQUESTS)
            .whereEqualTo("userId", userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e(TAG, "observeAIRequests failed: ${error.message}")
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                trySend(snapshot?.documents.orEmpty().mapNotNull { it.data })
            }
        awaitClose { registration.remove() }
    }

    override suspend fun upsertAIRequest(
        requestId: String,
        userId: String,
        prompt: String,
        response: String?,
        status: String,
        createdAt: Long
    ) {
        val request = mapOf(
            "id" to requestId,
            "userId" to userId,
            "prompt" to prompt,
            "response" to response,
            "status" to status,
            "createdAt" to createdAt,
            "updatedAt" to System.currentTimeMillis()
        )
        firestore.collection(CloudCollections.AI_REQUESTS).document(requestId).set(request, SetOptions.merge()).await()
    }

    override fun observeComments(postId: String): Flow<List<Map<String, Any?>>> = callbackFlow {
        val registration = firestore
            .collection(CloudCollections.COMMENTS)
            .whereEqualTo("postId", postId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e(TAG, "observeComments failed: ${error.message}")
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                trySend(snapshot?.documents.orEmpty().mapNotNull { it.data })
            }
        awaitClose { registration.remove() }
    }

    override suspend fun upsertComment(
        commentId: String,
        postId: String,
        userId: String,
        content: String,
        createdAt: Long
    ) {
        val comment = mapOf(
            "id" to commentId,
            "postId" to postId,
            "userId" to userId,
            "content" to content,
            "createdAt" to createdAt,
            "updatedAt" to System.currentTimeMillis()
        )
        firestore.collection(CloudCollections.COMMENTS).document(commentId).set(comment, SetOptions.merge()).await()
    }
}
