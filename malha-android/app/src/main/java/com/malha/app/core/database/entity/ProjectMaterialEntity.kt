package com.malha.app.core.database.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "project_materials",
    indices = [Index("projectId"), Index("materialId")]
)
data class ProjectMaterialEntity(
    @PrimaryKey val id: String,
    val projectId: String,
    val materialId: String,
    val plannedQuantity: Double?,
    val usedQuantity: Double?
)

