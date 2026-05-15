package com.malha.app.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "patterns")
data class PatternEntity(
    @PrimaryKey val id: String,
    val title: String,
    val description: String,
    val craftType: String,
    val difficulty: Int,
    val sourceType: String,
    val verificationStatus: String,
    val isPremium: Boolean,
    // Gauge
    val widthStitches: Int?,
    val heightRows: Int?,
    val measurementCm: Int?,
    
    val createdAt: Long,
    val updatedAt: Long
)
