package com.malha.app.core.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "reviews",
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
data class ReviewEntity(
    @PrimaryKey val id: String,
    val patternId: String,
    val userId: String,
    val rating: Int,
    val text: String,
    val createdAt: Long
)
