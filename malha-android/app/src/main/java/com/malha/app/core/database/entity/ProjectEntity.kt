package com.malha.app.core.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "projects",
    foreignKeys = [
        ForeignKey(
            entity = PatternEntity::class,
            parentColumns = ["id"],
            childColumns = ["patternId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [Index("patternId")]
)
data class ProjectEntity(
    @PrimaryKey val id: String,
    val name: String,
    val craftType: String,
    val patternId: String?,
    val status: String,
    val currentStepIndex: Int,
    val progressPercent: Int,
    val startedAt: Long?,
    val completedAt: Long?,
    val createdAt: Long,
    val updatedAt: Long
)

