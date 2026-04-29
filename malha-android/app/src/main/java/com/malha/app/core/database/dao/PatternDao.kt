package com.malha.app.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.malha.app.core.database.entity.PatternEntity
import com.malha.app.core.database.entity.PatternStepEntity
import com.malha.app.core.database.relation.PatternWithSteps
import kotlinx.coroutines.flow.Flow

@Dao
interface PatternDao {
    @Transaction
    @Query("SELECT * FROM patterns ORDER BY title ASC")
    fun observePatterns(): Flow<List<PatternWithSteps>>

    @Query("SELECT COUNT(*) FROM patterns")
    suspend fun countPatterns(): Int

    @Query("SELECT id FROM patterns ORDER BY title ASC LIMIT 1")
    suspend fun getFirstPatternId(): String?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPatterns(patterns: List<PatternEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPattern(pattern: PatternEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSteps(steps: List<PatternStepEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStep(step: PatternStepEntity)
}
