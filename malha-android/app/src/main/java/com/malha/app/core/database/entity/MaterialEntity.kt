package com.malha.app.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "materials")
data class MaterialEntity(
    @PrimaryKey val id: String,
    val type: String,
    val name: String,
    val imageUri: String?,
    val color: String?,
    val fiber: String?,
    val weight: String?,
    val quantity: Double,
    val unit: String,
    val lengthMeters: Double?,
    val costCents: Int?,
    val purchasedAt: Long?,
    val createdAt: Long,
    val updatedAt: Long
)
