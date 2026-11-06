package com.malha.app.core.database.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "techniques",
    indices = [
        Index("craftType"),
        Index("sourceType"),
        Index("isArchived")
    ]
)
data class TechniqueEntity(
    @PrimaryKey
    val id: String,
    val craftType: String, // KNITTING, CROCHET
    val canonicalNameKey: String,
    val descriptionKey: String? = null,
    val customName: String? = null,
    val customDescription: String? = null,
    val mediaUrl: String? = null,
    val sourceType: String = "OFFICIAL", // OFFICIAL, CUSTOM
    val ownerId: String? = null,
    val version: Int = 1,
    val isArchived: Boolean = false
)

@Entity(
    tableName = "technique_aliases",
    indices = [
        Index("techniqueId"),
        Index("normalizedAlias")
    ]
)
data class TechniqueAliasEntity(
    @PrimaryKey
    val id: String,
    val techniqueId: String,
    val normalizedAlias: String,
    val locale: String = "en",
    val displayAlias: String
)
