package com.malha.app.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.malha.app.domain.model.CraftType
import com.malha.app.domain.model.SkillLevel
import com.malha.app.domain.model.SourceType
import com.malha.app.domain.model.VerificationStatus

@Entity(tableName = "patterns")
data class PatternEntity(
    @PrimaryKey val id: String,
    val title: String,
    val designer: String?,
    val year: Int?,
    val craft: CraftType,
    val difficulty: SkillLevel,
    val gaugeWidth: Int?,
    val gaugeHeight: Int?,
    val gaugeUnit: String?,
    
    // Lifecycle & Source
    val sourceType: SourceType,
    val originalText: String?,
    val verificationStatus: VerificationStatus,
    val aiConfidence: Double,
    val isPremium: Boolean,
    
    // Multi-size (stored as CSV or JSON in real app, simplified for now)
    val availableSizes: String?,
    val selectedSize: String?,
    val updatedAt: Long
)
