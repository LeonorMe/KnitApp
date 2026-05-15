package com.malha.app.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.malha.app.core.database.dao.MaterialDao
import com.malha.app.core.database.dao.PatternDao
import com.malha.app.core.database.dao.ProjectDao
import com.malha.app.core.database.dao.ProjectStepProgressDao
import com.malha.app.core.database.entity.MaterialEntity
import com.malha.app.core.database.entity.PatternEntity
import com.malha.app.core.database.entity.PatternSectionEntity
import com.malha.app.core.database.entity.PatternStepEntity
import com.malha.app.core.database.entity.ProjectEntity
import com.malha.app.core.database.entity.ProjectMaterialEntity
import com.malha.app.core.database.entity.ProjectStepProgressEntity
import com.malha.app.core.database.entity.StitchPatternEntity
import com.malha.app.core.database.entity.UserPreferencesEntity

@Database(
    entities = [
        ProjectEntity::class,
        PatternEntity::class,
        PatternSectionEntity::class,
        PatternStepEntity::class,
        MaterialEntity::class,
        ProjectMaterialEntity::class,
        ProjectStepProgressEntity::class,
        UserPreferencesEntity::class,
        StitchPatternEntity::class
    ],
    version = 4,
    exportSchema = false
)
@TypeConverters(RoomConverters::class)
abstract class MalhaDatabase : RoomDatabase() {
    abstract fun projectDao(): ProjectDao
    abstract fun patternDao(): PatternDao
    abstract fun materialDao(): MaterialDao
    abstract fun projectStepProgressDao(): ProjectStepProgressDao
}
