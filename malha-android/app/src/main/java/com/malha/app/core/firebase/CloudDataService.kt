package com.malha.app.core.firebase

import com.malha.app.domain.model.MaterialType
import kotlinx.coroutines.flow.Flow
import android.net.Uri

interface CloudDataService {
    fun observeUserProfile(userId: String): Flow<Map<String, Any?>?>
    suspend fun upsertUserProfile(user: AuthUser)
    suspend fun updateUserProfile(
        userId: String,
        username: String?,
        bio: String?,
        profilePhotoUri: String?
    )
    suspend fun uploadProfilePhoto(
        userId: String,
        localUri: Uri
    ): String
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
}
