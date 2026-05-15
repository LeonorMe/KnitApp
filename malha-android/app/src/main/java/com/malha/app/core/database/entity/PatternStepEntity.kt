package com.malha.app.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.malha.app.domain.model.StepType

@Entity(tableName = "pattern_steps")
data class PatternStepEntity(
    @PrimaryKey val id: String,
    val patternId: String,
    val sectionId: String,
    val orderIndex: Int,
    val stepType: StepType,
    val instruction: String,
    val rowNumber: Int?,
    val stitchCount: Int?,
    val confidence: Double,
    
    // Execution Logic Parameters
    val repeatCount: Int?,
    val everyNRows: Int?,
    val startRow: Int?,
    val endRow: Int?,
    val condition: String?,
    val stitchPatternId: String?,
    val updatedAt: Long
)
