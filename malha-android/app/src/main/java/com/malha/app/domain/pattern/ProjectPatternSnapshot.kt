package com.malha.app.domain.pattern

data class ProjectPatternSnapshot(
    val id: String,
    val projectId: String,
    val sourcePatternId: String,
    val sourceVersionId: String,
    val selectedSizeKey: String,
    val schemaVersion: Int = 1,
    val checksum: String,
    val totalInstructionCount: Int,
    val createdAt: Long = System.currentTimeMillis(),
    val instructions: List<ProjectInstructionSnapshot> = emptyList()
)

data class ProjectInstructionSnapshot(
    val id: String,
    val snapshotId: String,
    val ordinal: Int,
    val stablePath: String,
    val type: InstructionNodeType,
    val techniqueId: String? = null,
    val targetCount: Int? = null,
    val countUnit: CountUnit = CountUnit.STITCH,
    val sectionTitle: String,
    val displayText: String,
    val measurementValue: Float? = null,
    val measurementUnit: String? = null,
    val isDestructive: Boolean = false
)

data class InstructionProgress(
    val projectId: String,
    val instructionSnapshotId: String,
    val currentCount: Int = 0,
    val isCompleted: Boolean = false,
    val completedAt: Long? = null,
    val selectedIncrement: Int = 1,
    val note: String? = null
)
