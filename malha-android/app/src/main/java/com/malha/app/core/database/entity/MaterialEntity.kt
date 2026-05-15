package com.malha.app.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "materials")
data class MaterialEntity(
    @PrimaryKey val id: String,
    val name: String,
    val imageUri: String?,
    val type: String,
    val quantity: Double,
    val unit: String,
    
    // Structured Data
    val fiber: String?,
    val gramsPerBall: Int?,
    val needleType: String?,
    val sizeMm: Double?,
    val lengthCm: Int?
)
