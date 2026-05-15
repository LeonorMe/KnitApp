package com.malha.app.core.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "pattern_steps",
    foreignKeys = [
        ForeignKey(
            entity = PatternEntity::class,
            parentColumns = ["id"],
            childColumns = ["patternId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = PatternSectionEntity::class,
            parentColumns = ["id"],
            childColumns = ["sectionId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [Index("patternId"), Index("sectionId")]
)
data class PatternStepEntity(
    @PrimaryKey val id: String,
    val patternId: String,
    val sectionId: String?,
    val orderIndex: Int,
    val rowNumber: Int?,
    val instruction: String,
    val stepType: String,
    val stitchCount: Int?,
    val confidence: Double,
    val createdAt: Long,
    val updatedAt: Long
)
