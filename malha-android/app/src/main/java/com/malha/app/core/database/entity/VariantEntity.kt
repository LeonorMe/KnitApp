package com.malha.app.core.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "variants",
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
data class VariantEntity(
    @PrimaryKey val id: String,
    val patternId: String,
    val userId: String,
    val name: String,
    val notes: String?,
    val updatedAt: Long
)
