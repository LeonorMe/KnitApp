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
        )
    ],
    indices = [Index("patternId")]
)
data class PatternStepEntity(
    @PrimaryKey val id: String,
    val patternId: String,
    val orderIndex: Int,
    val rowNumber: Int?,
    val instruction: String,
    val stepType: String?,
    val repeatInfo: String?,
    val createdAt: Long,
    val updatedAt: Long
)

