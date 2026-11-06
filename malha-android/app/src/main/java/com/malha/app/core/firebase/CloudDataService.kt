package com.malha.app.core.firebase

import com.malha.app.domain.model.MaterialType
import kotlinx.coroutines.flow.Flow
import android.net.Uri
import com.malha.app.domain.model.Post

interface CloudDataService {
    fun observeUserProfile(userId: String): Flow<Map<String, Any?>?>
    suspend fun upsertUserProfile(user: AuthUser)
    suspend fun updateUserProfile(
        userId: String,
        username: String?,
        bio: String?,
        profilePhotoUri: String?,
        coins: Int
    )
    suspend fun uploadProfilePhoto(
        userId: String,
        localUri: Uri
    ): String

    suspend fun updateUserCoins(userId: String, delta: Int)

    suspend fun upsertProject(
        userId: String,
        projectId: String,
        name: String,
        patternId: String?,
        currentStepIndex: Int,
        progressPercent: Int,
        status: String
    )
    suspend fun updateProjectColor(userId: String, projectId: String, colorHex: String?)
    suspend fun deleteProject(userId: String, projectId: String)
    suspend fun upsertMaterial(
        userId: String,
        materialId: String,
        name: String,
        type: MaterialType,
        quantity: Double,
        unit: String,
        imageUri: String?
    )
    suspend fun deleteMaterial(userId: String, materialId: String)

    suspend fun createPost(
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
    ): String

    fun observePosts(): Flow<List<Post>>

    // Projects & Materials sync
    fun observeProjects(userId: String): Flow<List<Map<String, Any?>>>
    fun observeMaterials(userId: String): Flow<List<Map<String, Any?>>>

    // Patterns & Steps sync
    fun observePatterns(): Flow<List<Map<String, Any?>>>
    fun observePatternSteps(): Flow<List<Map<String, Any?>>>
    suspend fun upsertPattern(
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
    )
    suspend fun upsertPatternStep(
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
    )

    // Project Materials
    fun observeProjectMaterials(userId: String): Flow<List<Map<String, Any?>>>
    suspend fun upsertProjectMaterial(
        userId: String,
        id: String,
        projectId: String,
        materialId: String,
        plannedQuantity: Double?,
        usedQuantity: Double?
    )

    // Variants
    fun observeVariants(patternId: String): Flow<List<Map<String, Any?>>>
    suspend fun upsertVariant(
        variantId: String,
        patternId: String,
        userId: String,
        name: String,
        notes: String?,
        updatedAt: Long
    )

    // Reviews
    fun observeReviews(patternId: String): Flow<List<Map<String, Any?>>>
    suspend fun upsertReview(
        reviewId: String,
        patternId: String,
        userId: String,
        rating: Int,
        text: String,
        createdAt: Long
    )

    // AI Requests
    fun observeAIRequests(userId: String): Flow<List<Map<String, Any?>>>
    suspend fun upsertAIRequest(
        requestId: String,
        userId: String,
        prompt: String,
        response: String?,
        status: String,
        createdAt: Long
    )

    // Comments
    fun observeComments(postId: String): Flow<List<Map<String, Any?>>>
    suspend fun upsertComment(
        commentId: String,
        postId: String,
        userId: String,
        content: String,
        createdAt: Long
    )
}
