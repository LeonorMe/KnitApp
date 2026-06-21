package com.malha.app.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.malha.app.core.database.entity.AIRequestEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AIRequestDao {
    @Query("SELECT * FROM ai_requests WHERE userId = :userId ORDER BY createdAt DESC")
    fun observeAIRequests(userId: String): Flow<List<AIRequestEntity>>

    @Query("SELECT * FROM ai_requests WHERE id = :requestId LIMIT 1")
    fun observeAIRequest(requestId: String): Flow<AIRequestEntity?>

    @Query("SELECT * FROM ai_requests WHERE id = :requestId LIMIT 1")
    suspend fun getAIRequest(requestId: String): AIRequestEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAIRequests(requests: List<AIRequestEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAIRequest(request: AIRequestEntity)
}
