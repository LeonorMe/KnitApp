package com.malha.app.core.database.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "project_pattern_snapshots",
    indices = [
        Index("projectId", unique = true),
        Index("sourcePatternId")
    ]
)
data class ProjectPatternSnapshotEntity(
    @PrimaryKey
    val id: String,
    val projectId: String,
    val sourcePatternId: String,
    val sourceVersionId: String,
    val selectedSizeKey: String,
    val schemaVersion: Int = 1,
    val checksum: String,
    val totalInstructionCount: Int,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "project_instruction_snapshots",
    indices = [
        Index("snapshotId"),
        Index("snapshotId", "ordinal")
    ]
)
data class ProjectInstructionSnapshotEntity(
    @PrimaryKey
    val id: String,
    val snapshotId: String,
    val ordinal: Int,
    val stablePath: String,
    val type: String,
    val techniqueId: String? = null,
    val targetCount: Int? = null,
    val countUnit: String = "STITCH",
    val sectionTitle: String,
    val displayText: String,
    val measurementValue: Float? = null,
    val measurementUnit: String? = null,
    val isDestructive: Boolean = false
)

@Entity(
    tableName = "instruction_progress",
    indices = [
        Index("projectId"),
        Index("instructionSnapshotId"),
        Index("projectId", "instructionSnapshotId", unique = true)
    ]
)
data class InstructionProgressEntity(
    @PrimaryKey
    val id: String,
    val projectId: String,
    val instructionSnapshotId: String,
    val currentCount: Int = 0,
    val isCompleted: Boolean = false,
    val completedAt: Long? = null,
    val selectedIncrement: Int = 1,
    val note: String? = null
)
