package com.malha.app.core.database.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "pattern_versions",
    indices = [
        Index("patternId")
    ]
)
data class PatternVersionEntity(
    @PrimaryKey
    val id: String,
    val patternId: String,
    val versionNumber: Int = 1,
    val schemaVersion: Int = 1,
    val status: String = "PUBLISHED",
    val createdAt: Long = System.currentTimeMillis(),
    val checksum: String = ""
)

@Entity(
    tableName = "pattern_sizes",
    indices = [
        Index("patternVersionId")
    ]
)
data class PatternSizeEntity(
    @PrimaryKey
    val id: String,
    val patternVersionId: String,
    val sizeKey: String,
    val displayName: String,
    val orderIndex: Int = 0,
    val measurementsJson: String? = null
)

@Entity(
    tableName = "pattern_parameters",
    indices = [
        Index("patternVersionId"),
        Index("sizeKey")
    ]
)
data class PatternParameterEntity(
    @PrimaryKey
    val id: String,
    val patternVersionId: String,
    val sizeKey: String,
    val paramKey: String,
    val value: String,
    val unit: String? = null
)

@Entity(
    tableName = "instruction_nodes",
    indices = [
        Index("sectionId"),
        Index("parentNodeId")
    ]
)
data class InstructionNodeEntity(
    @PrimaryKey
    val id: String,
    val sectionId: String,
    val parentNodeId: String? = null,
    val orderIndex: Int,
    val nodeType: String,
    val techniqueId: String? = null,
    val targetLiteral: Int? = null,
    val targetParamKey: String? = null,
    val countUnit: String = "STITCH",
    val repeatTimesLiteral: Int? = null,
    val repeatTimesParamKey: String? = null,
    val displayText: String? = null,
    val measurementValue: Float? = null,
    val measurementUnit: String? = null,
    val isDestructive: Boolean = false
)
