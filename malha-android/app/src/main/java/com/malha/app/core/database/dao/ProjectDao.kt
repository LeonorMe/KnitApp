package com.malha.app.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.malha.app.core.database.entity.ProjectEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProjectDao {
    @Query("SELECT * FROM projects WHERE status = 'in_progress' OR status = 'active' ORDER BY updatedAt DESC")
    fun observeActiveProjects(): Flow<List<ProjectEntity>>

    @Query("SELECT * FROM projects WHERE status != 'archived' ORDER BY updatedAt DESC")
    fun observeAllProjects(): Flow<List<ProjectEntity>>

    @Query("SELECT * FROM projects WHERE id = :projectId LIMIT 1")
    fun observeProject(projectId: String): Flow<ProjectEntity?>

    @Query("SELECT * FROM projects WHERE id = :projectId LIMIT 1")
    suspend fun getProjectById(projectId: String): ProjectEntity?

    @Query("SELECT COUNT(*) FROM projects")
    suspend fun countProjects(): Int

    @Query("SELECT COUNT(*) > 0 FROM projects WHERE id = :projectId")
    suspend fun projectExistsById(projectId: String): Boolean

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProjects(projects: List<ProjectEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProject(project: ProjectEntity)

    @Query("UPDATE projects SET status = 'archived', updatedAt = :updatedAt WHERE id = :projectId")
    suspend fun archiveProject(projectId: String, updatedAt: Long): Int

    @Query("DELETE FROM projects WHERE id = :projectId")
    suspend fun deleteProject(projectId: String): Int

    @Query(
        """
        UPDATE projects
        SET status = 'finished',
            progressPercent = 100,
            completedAt = :completedAt,
            updatedAt = :completedAt,
            imageUri = COALESCE(:imageUri, imageUri)
        WHERE id = :projectId
        """
    )
    suspend fun completeProject(projectId: String, completedAt: Long, imageUri: String?): Int

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
    ): Int

    @Query("UPDATE projects SET colorHex = :colorHex, updatedAt = :updatedAt WHERE id = :projectId")
    suspend fun updateColor(projectId: String, colorHex: String?, updatedAt: Long): Int

    @Query("UPDATE projects SET status = :status, startedAt = COALESCE(startedAt, :startedAt), updatedAt = :updatedAt WHERE id = :projectId")
    suspend fun updateProjectStatus(projectId: String, status: String, startedAt: Long?, updatedAt: Long): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProjectMaterials(projectMaterials: List<com.malha.app.core.database.entity.ProjectMaterialEntity>)

    @Query("SELECT * FROM project_materials WHERE projectId = :projectId")
    fun observeProjectMaterials(projectId: String): Flow<List<com.malha.app.core.database.entity.ProjectMaterialEntity>>

    @Query("SELECT * FROM project_materials WHERE projectId = :projectId")
    suspend fun getProjectMaterials(projectId: String): List<com.malha.app.core.database.entity.ProjectMaterialEntity>

    @Query("DELETE FROM project_materials WHERE projectId = :projectId")
    suspend fun deleteProjectMaterials(projectId: String)

    @Query(
        """
        UPDATE projects
        SET name = :name,
            colorHex = :colorHex,
            selectedSize = :selectedSize,
            patternId = :patternId,
            patternTitle = :patternTitle,
            patternSnapshotJson = :patternSnapshotJson,
            status = :status,
            updatedAt = :updatedAt
        WHERE id = :projectId
        """
    )
    suspend fun updateProjectSettingsWithPattern(
        projectId: String,
        name: String,
        colorHex: String?,
        selectedSize: String?,
        patternId: String?,
        patternTitle: String?,
        patternSnapshotJson: String?,
        status: String,
        updatedAt: Long
    ): Int

    @Query(
        """
        UPDATE projects
        SET name = :name,
            colorHex = :colorHex,
            selectedSize = :selectedSize,
            updatedAt = :updatedAt
        WHERE id = :projectId
        """
    )
    suspend fun updateProjectSettings(
        projectId: String,
        name: String,
        colorHex: String?,
        selectedSize: String?,
        updatedAt: Long
    ): Int

}
