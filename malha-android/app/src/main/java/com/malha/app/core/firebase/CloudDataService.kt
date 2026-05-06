package com.malha.app.core.firebase

import kotlinx.coroutines.flow.Flow

interface CloudDataService {
    fun observeUserProfile(userId: String): Flow<Map<String, Any?>?>
    suspend fun upsertUserProfile(user: AuthUser)
}

