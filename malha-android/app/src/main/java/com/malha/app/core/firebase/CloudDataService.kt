package com.malha.app.core.firebase

import com.malha.app.domain.model.MaterialType
import kotlinx.coroutines.flow.Flow
import android.net.Uri
import com.malha.app.domain.model.Post
import java.util.UUID

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
    suspend fun upsertMaterial(
        userId: String,
        materialId: String,
        name: String,
        type: MaterialType,
        quantity: Double,
        unit: String,
        imageUri: String?
    )

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
}
