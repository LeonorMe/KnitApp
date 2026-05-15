package com.malha.app.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "stitch_library")
data class StitchPatternEntity(
    @PrimaryKey val id: String,
    val name: String,
    val description: String,
    val instructions: String,
    val imageUrl: String?,
    val videoUrl: String?,
    val searchTerms: String // Comma separated terms
)
