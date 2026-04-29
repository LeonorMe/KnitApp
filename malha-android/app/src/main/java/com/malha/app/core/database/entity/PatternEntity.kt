package com.malha.app.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "patterns")
data class PatternEntity(
    @PrimaryKey val id: String,
    val title: String,
    val description: String,
    val craftType: String,
    val difficulty: String,
    val sourceType: String,
    val verificationStatus: String,
    val isPremium: Boolean,
    val createdAt: Long,
    val updatedAt: Long
)

