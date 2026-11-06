package com.malha.app.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.malha.app.core.database.entity.InstructionProgressEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface InstructionProgressDao {

    @Query("SELECT * FROM instruction_progress WHERE projectId = :projectId")
    suspend fun getProgressForProject(projectId: String): List<InstructionProgressEntity>

    @Query("SELECT * FROM instruction_progress WHERE projectId = :projectId")
    fun observeProgressForProject(projectId: String): Flow<List<InstructionProgressEntity>>

    @Query("SELECT * FROM instruction_progress WHERE projectId = :projectId AND instructionSnapshotId = :instructionId LIMIT 1")
    suspend fun getProgressForInstruction(projectId: String, instructionId: String): InstructionProgressEntity?

    @Query("SELECT * FROM instruction_progress WHERE projectId = :projectId AND instructionSnapshotId = :instructionId LIMIT 1")
    fun observeProgressForInstruction(projectId: String, instructionId: String): Flow<InstructionProgressEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProgress(progress: InstructionProgressEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllProgress(progressList: List<InstructionProgressEntity>)

    @Query("""
        UPDATE instruction_progress 
        SET currentCount = :count, selectedIncrement = :increment 
        WHERE projectId = :projectId AND instructionSnapshotId = :instructionId
    """)
    suspend fun updateCount(projectId: String, instructionId: String, count: Int, increment: Int)

    @Query("""
        UPDATE instruction_progress 
        SET isCompleted = :completed, completedAt = :completedAt 
        WHERE projectId = :projectId AND instructionSnapshotId = :instructionId
    """)
    suspend fun markCompleted(projectId: String, instructionId: String, completed: Boolean, completedAt: Long?)

    @Query("""
        UPDATE instruction_progress 
        SET note = :note 
        WHERE projectId = :projectId AND instructionSnapshotId = :instructionId
    """)
    suspend fun updateNote(projectId: String, instructionId: String, note: String?)

    @Query("DELETE FROM instruction_progress WHERE projectId = :projectId")
    suspend fun deleteProgressForProject(projectId: String)
}
