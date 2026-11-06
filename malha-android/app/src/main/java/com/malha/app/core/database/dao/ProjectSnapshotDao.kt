package com.malha.app.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.malha.app.core.database.entity.ProjectInstructionSnapshotEntity
import com.malha.app.core.database.entity.ProjectPatternSnapshotEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProjectSnapshotDao {

    @Query("SELECT * FROM project_pattern_snapshots WHERE projectId = :projectId LIMIT 1")
    suspend fun getSnapshotForProject(projectId: String): ProjectPatternSnapshotEntity?

    @Query("SELECT * FROM project_pattern_snapshots WHERE projectId = :projectId LIMIT 1")
    fun observeSnapshotForProject(projectId: String): Flow<ProjectPatternSnapshotEntity?>

    @Query("SELECT * FROM project_instruction_snapshots WHERE snapshotId = :snapshotId ORDER BY ordinal ASC")
    suspend fun getInstructionsForSnapshot(snapshotId: String): List<ProjectInstructionSnapshotEntity>

    @Query("SELECT * FROM project_instruction_snapshots WHERE snapshotId = :snapshotId ORDER BY ordinal ASC")
    fun observeInstructionsForSnapshot(snapshotId: String): Flow<List<ProjectInstructionSnapshotEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSnapshot(snapshot: ProjectPatternSnapshotEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInstructions(instructions: List<ProjectInstructionSnapshotEntity>)

    @Query("DELETE FROM project_pattern_snapshots WHERE projectId = :projectId")
    suspend fun deleteSnapshotForProject(projectId: String)

    @Query("DELETE FROM project_instruction_snapshots WHERE snapshotId = :snapshotId")
    suspend fun deleteInstructionsForSnapshot(snapshotId: String)
}
