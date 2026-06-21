package com.malha.app.core.database.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "ai_requests",
    indices = [Index("userId")]
)
data class AIRequestEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val prompt: String,
    val response: String?,
    val status: String,
    val createdAt: Long
)
