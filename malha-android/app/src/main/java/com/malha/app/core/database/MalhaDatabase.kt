package com.malha.app.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.malha.app.core.database.dao.MaterialDao
import com.malha.app.core.database.dao.PatternDao
import com.malha.app.core.database.dao.ProjectDao
import com.malha.app.core.database.entity.MaterialEntity
import com.malha.app.core.database.entity.PatternEntity
import com.malha.app.core.database.entity.PatternStepEntity
import com.malha.app.core.database.entity.ProjectEntity
import com.malha.app.core.database.entity.ProjectMaterialEntity
import com.malha.app.core.database.entity.ProjectStepProgressEntity
import com.malha.app.core.database.entity.UserPreferencesEntity

@Database(
    entities = [
        ProjectEntity::class,
        PatternEntity::class,
        PatternStepEntity::class,
        MaterialEntity::class,
        ProjectMaterialEntity::class,
        ProjectStepProgressEntity::class,
        UserPreferencesEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class MalhaDatabase : RoomDatabase() {
    abstract fun projectDao(): ProjectDao
    abstract fun patternDao(): PatternDao
    abstract fun materialDao(): MaterialDao
}
