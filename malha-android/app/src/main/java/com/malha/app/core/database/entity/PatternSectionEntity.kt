package com.malha.app.core.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "pattern_sections",
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
data class PatternSectionEntity(
    @PrimaryKey val id: String,
    val patternId: String,
    val name: String,
    val orderIndex: Int
)
