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
        CommentEntity::class,
        VariantEntity::class,
        ReviewEntity::class,
        AIRequestEntity::class
    ],
    version = 12,
    exportSchema = false
)
@TypeConverters(RoomConverters::class)
abstract class MalhaDatabase : RoomDatabase() {
    abstract fun projectDao(): ProjectDao
    abstract fun patternDao(): PatternDao
    abstract fun materialDao(): MaterialDao
    abstract fun projectStepProgressDao(): ProjectStepProgressDao
    abstract fun socialDao(): SocialDao
    abstract fun variantDao(): VariantDao
    abstract fun reviewDao(): ReviewDao
    abstract fun aiRequestDao(): AIRequestDao

    companion object {
        val MIGRATION_6_7 = object : Migration(6, 7) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE projects ADD COLUMN colorHex TEXT DEFAULT NULL")
            }
        }
        val MIGRATION_7_8 = object : Migration(7, 8) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE materials ADD COLUMN weightCategory TEXT DEFAULT NULL")
                database.execSQL("ALTER TABLE projects ADD COLUMN selectedSize TEXT DEFAULT NULL")
            }
        }
        // Recreate project_materials without FK constraints to prevent SQLiteConstraintException
        // during Firebase sync when parent rows haven't arrived yet.
        val MIGRATION_10_11 = object : Migration(10, 11) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("DROP TABLE IF EXISTS `project_materials`")
                database.execSQL("""
                    CREATE TABLE IF NOT EXISTS `project_materials` (
                        `id` TEXT NOT NULL,
                        `projectId` TEXT NOT NULL,
                        `materialId` TEXT NOT NULL,
                        `plannedQuantity` REAL,
                        `usedQuantity` REAL,
                        PRIMARY KEY(`id`)
                    )
                """.trimIndent())
                database.execSQL("CREATE INDEX IF NOT EXISTS `index_project_materials_projectId` ON `project_materials` (`projectId`)")
                database.execSQL("CREATE INDEX IF NOT EXISTS `index_project_materials_materialId` ON `project_materials` (`materialId`)")
            }
        }
        val MIGRATION_11_12 = object : Migration(11, 12) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE projects ADD COLUMN patternTitle TEXT DEFAULT NULL")
                database.execSQL("ALTER TABLE projects ADD COLUMN patternSnapshotJson TEXT DEFAULT NULL")
            }
        }
    }
}


