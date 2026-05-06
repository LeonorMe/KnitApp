package com.malha.app.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.malha.app.core.database.entity.ProjectEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProjectDao {
    @Query("SELECT * FROM projects WHERE status != 'archived' ORDER BY updatedAt DESC")
    fun observeActiveProjects(): Flow<List<ProjectEntity>>

    @Query("SELECT * FROM projects WHERE id = :projectId LIMIT 1")
    fun observeProject(projectId: String): Flow<ProjectEntity?>

    @Query("SELECT COUNT(*) FROM projects")
    suspend fun countProjects(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProjects(projects: List<ProjectEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProject(project: ProjectEntity)

    @Query("UPDATE projects SET status = 'archived', updatedAt = :updatedAt WHERE id = :projectId")
    suspend fun archiveProject(projectId: String, updatedAt: Long)

    @Query(
        """
        UPDATE projects
        SET currentStepIndex = :stepIndex,
            progressPercent = :progressPercent,
            updatedAt = :updatedAt
        WHERE id = :projectId
        """
    )
    suspend fun updateProgress(
        projectId: String,
        stepIndex: Int,
        progressPercent: Int,
        updatedAt: Long
    )
}
