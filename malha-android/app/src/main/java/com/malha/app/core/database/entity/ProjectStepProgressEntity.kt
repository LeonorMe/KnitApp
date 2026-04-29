package com.malha.app.core.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "project_step_progress",
    foreignKeys = [
        ForeignKey(
            entity = ProjectEntity::class,
            parentColumns = ["id"],
            childColumns = ["projectId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = PatternStepEntity::class,
            parentColumns = ["id"],
            childColumns = ["patternStepId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("projectId"), Index("patternStepId")]
)
data class ProjectStepProgressEntity(
    @PrimaryKey val id: String,
    val projectId: String,
    val patternStepId: String,
    val isDone: Boolean,
    val doneAt: Long?,
    val note: String?
)

