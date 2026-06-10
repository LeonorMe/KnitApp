package com.malha.app.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.malha.app.core.database.dao.*
import com.malha.app.core.database.entity.*

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
        StitchPatternEntity::class,
        UserEntity::class,
        PostEntity::class,
        CommentEntity::class
    ],
    version = 7,
    exportSchema = false
)
@TypeConverters(RoomConverters::class)
abstract class MalhaDatabase : RoomDatabase() {
    abstract fun projectDao(): ProjectDao
    abstract fun patternDao(): PatternDao
    abstract fun materialDao(): MaterialDao
    abstract fun projectStepProgressDao(): ProjectStepProgressDao
    abstract fun socialDao(): SocialDao

    companion object {
        val MIGRATION_6_7 = object : Migration(6, 7) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE projects ADD COLUMN colorHex TEXT DEFAULT NULL")
            }
        }
    }
}
