package com.malha.app.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.malha.app.core.database.entity.ProjectStepProgressEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProjectStepProgressDao {
    @Query(
        """
        SELECT * FROM project_step_progress
        WHERE projectId = :projectId AND patternStepId = :patternStepId
        LIMIT 1
        """
    )
    fun observeStepProgress(
        projectId: String,
        patternStepId: String
    ): Flow<ProjectStepProgressEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertStepProgress(progress: ProjectStepProgressEntity)
}

