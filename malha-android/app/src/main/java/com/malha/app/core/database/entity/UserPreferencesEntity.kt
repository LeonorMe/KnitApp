package com.malha.app.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_preferences")
data class UserPreferencesEntity(
    @PrimaryKey val id: String = "local",
    val language: String,
    val unitSystem: String,
    val knittingNotation: String,
    val crochetTerminology: String,
    val themeMode: String
)

