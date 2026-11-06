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
        AIRequestEntity::class,
        TechniqueEntity::class,
        TechniqueAliasEntity::class,
        PatternVersionEntity::class,
        PatternSizeEntity::class,
        PatternParameterEntity::class,
        InstructionNodeEntity::class,
        ProjectPatternSnapshotEntity::class,
        ProjectInstructionSnapshotEntity::class,
        InstructionProgressEntity::class
    ],
    version = 13,
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
    abstract fun techniqueDao(): TechniqueDao
    abstract fun patternVersionDao(): PatternVersionDao
    abstract fun projectSnapshotDao(): ProjectSnapshotDao
    abstract fun instructionProgressDao(): InstructionProgressDao

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
        val MIGRATION_12_13 = object : Migration(12, 13) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // 1. Create techniques table
                database.execSQL("""
                    CREATE TABLE IF NOT EXISTS `techniques` (
                        `id` TEXT NOT NULL,
                        `craftType` TEXT NOT NULL,
                        `canonicalNameKey` TEXT NOT NULL,
                        `descriptionKey` TEXT,
                        `customName` TEXT,
                        `customDescription` TEXT,
                        `mediaUrl` TEXT,
                        `sourceType` TEXT NOT NULL,
                        `ownerId` TEXT,
                        `version` INTEGER NOT NULL,
                        `isArchived` INTEGER NOT NULL,
                        PRIMARY KEY(`id`)
                    )
                """.trimIndent())
                database.execSQL("CREATE INDEX IF NOT EXISTS `index_techniques_craftType` ON `techniques` (`craftType`)")
                database.execSQL("CREATE INDEX IF NOT EXISTS `index_techniques_sourceType` ON `techniques` (`sourceType`)")
                database.execSQL("CREATE INDEX IF NOT EXISTS `index_techniques_isArchived` ON `techniques` (`isArchived`)")

                // 2. Create technique_aliases table
                database.execSQL("""
                    CREATE TABLE IF NOT EXISTS `technique_aliases` (
                        `id` TEXT NOT NULL,
                        `techniqueId` TEXT NOT NULL,
                        `normalizedAlias` TEXT NOT NULL,
                        `locale` TEXT NOT NULL,
                        `displayAlias` TEXT NOT NULL,
                        PRIMARY KEY(`id`)
                    )
                """.trimIndent())
                database.execSQL("CREATE INDEX IF NOT EXISTS `index_technique_aliases_techniqueId` ON `technique_aliases` (`techniqueId`)")
                database.execSQL("CREATE INDEX IF NOT EXISTS `index_technique_aliases_normalizedAlias` ON `technique_aliases` (`normalizedAlias`)")

                // 3. Create pattern_versions table
                database.execSQL("""
                    CREATE TABLE IF NOT EXISTS `pattern_versions` (
                        `id` TEXT NOT NULL,
                        `patternId` TEXT NOT NULL,
                        `versionNumber` INTEGER NOT NULL,
                        `schemaVersion` INTEGER NOT NULL,
                        `status` TEXT NOT NULL,
                        `createdAt` INTEGER NOT NULL,
                        `checksum` TEXT NOT NULL,
                        PRIMARY KEY(`id`)
                    )
                """.trimIndent())
                database.execSQL("CREATE INDEX IF NOT EXISTS `index_pattern_versions_patternId` ON `pattern_versions` (`patternId`)")

                // 4. Create pattern_sizes table
                database.execSQL("""
                    CREATE TABLE IF NOT EXISTS `pattern_sizes` (
                        `id` TEXT NOT NULL,
                        `patternVersionId` TEXT NOT NULL,
                        `sizeKey` TEXT NOT NULL,
                        `displayName` TEXT NOT NULL,
                        `orderIndex` INTEGER NOT NULL,
                        `measurementsJson` TEXT,
                        PRIMARY KEY(`id`)
                    )
                """.trimIndent())
                database.execSQL("CREATE INDEX IF NOT EXISTS `index_pattern_sizes_patternVersionId` ON `pattern_sizes` (`patternVersionId`)")

                // 5. Create pattern_parameters table
                database.execSQL("""
                    CREATE TABLE IF NOT EXISTS `pattern_parameters` (
                        `id` TEXT NOT NULL,
                        `patternVersionId` TEXT NOT NULL,
                        `sizeKey` TEXT NOT NULL,
                        `paramKey` TEXT NOT NULL,
                        `value` TEXT NOT NULL,
                        `unit` TEXT,
                        PRIMARY KEY(`id`)
                    )
                """.trimIndent())
                database.execSQL("CREATE INDEX IF NOT EXISTS `index_pattern_parameters_patternVersionId` ON `pattern_parameters` (`patternVersionId`)")
                database.execSQL("CREATE INDEX IF NOT EXISTS `index_pattern_parameters_sizeKey` ON `pattern_parameters` (`sizeKey`)")

                // 6. Create instruction_nodes table
                database.execSQL("""
                    CREATE TABLE IF NOT EXISTS `instruction_nodes` (
                        `id` TEXT NOT NULL,
                        `sectionId` TEXT NOT NULL,
                        `parentNodeId` TEXT,
                        `orderIndex` INTEGER NOT NULL,
                        `nodeType` TEXT NOT NULL,
                        `techniqueId` TEXT,
                        `targetLiteral` INTEGER,
                        `targetParamKey` TEXT,
                        `countUnit` TEXT NOT NULL,
                        `repeatTimesLiteral` INTEGER,
                        `repeatTimesParamKey` TEXT,
                        `displayText` TEXT,
                        `measurementValue` REAL,
                        `measurementUnit` TEXT,
                        `isDestructive` INTEGER NOT NULL,
                        PRIMARY KEY(`id`)
                    )
                """.trimIndent())
                database.execSQL("CREATE INDEX IF NOT EXISTS `index_instruction_nodes_sectionId` ON `instruction_nodes` (`sectionId`)")
                database.execSQL("CREATE INDEX IF NOT EXISTS `index_instruction_nodes_parentNodeId` ON `instruction_nodes` (`parentNodeId`)")

                // 7. Create project_pattern_snapshots table
                database.execSQL("""
                    CREATE TABLE IF NOT EXISTS `project_pattern_snapshots` (
                        `id` TEXT NOT NULL,
                        `projectId` TEXT NOT NULL,
                        `sourcePatternId` TEXT NOT NULL,
                        `sourceVersionId` TEXT NOT NULL,
                        `selectedSizeKey` TEXT NOT NULL,
                        `schemaVersion` INTEGER NOT NULL,
                        `checksum` TEXT NOT NULL,
                        `totalInstructionCount` INTEGER NOT NULL,
                        `createdAt` INTEGER NOT NULL,
                        PRIMARY KEY(`id`)
                    )
                """.trimIndent())
                database.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_project_pattern_snapshots_projectId` ON `project_pattern_snapshots` (`projectId`)")
                database.execSQL("CREATE INDEX IF NOT EXISTS `index_project_pattern_snapshots_sourcePatternId` ON `project_pattern_snapshots` (`sourcePatternId`)")

                // 8. Create project_instruction_snapshots table
                database.execSQL("""
                    CREATE TABLE IF NOT EXISTS `project_instruction_snapshots` (
                        `id` TEXT NOT NULL,
                        `snapshotId` TEXT NOT NULL,
                        `ordinal` INTEGER NOT NULL,
                        `stablePath` TEXT NOT NULL,
                        `type` TEXT NOT NULL,
                        `techniqueId` TEXT,
                        `targetCount` INTEGER,
                        `countUnit` TEXT NOT NULL,
                        `sectionTitle` TEXT NOT NULL,
                        `displayText` TEXT NOT NULL,
                        `measurementValue` REAL,
                        `measurementUnit` TEXT,
                        `isDestructive` INTEGER NOT NULL,
                        PRIMARY KEY(`id`)
                    )
                """.trimIndent())
                database.execSQL("CREATE INDEX IF NOT EXISTS `index_project_instruction_snapshots_snapshotId` ON `project_instruction_snapshots` (`snapshotId`)")
                database.execSQL("CREATE INDEX IF NOT EXISTS `index_project_instruction_snapshots_snapshotId_ordinal` ON `project_instruction_snapshots` (`snapshotId`, `ordinal`)")

                // 9. Create instruction_progress table
                database.execSQL("""
                    CREATE TABLE IF NOT EXISTS `instruction_progress` (
                        `id` TEXT NOT NULL,
                        `projectId` TEXT NOT NULL,
                        `instructionSnapshotId` TEXT NOT NULL,
                        `currentCount` INTEGER NOT NULL,
                        `isCompleted` INTEGER NOT NULL,
                        `completedAt` INTEGER,
                        `selectedIncrement` INTEGER NOT NULL,
                        `note` TEXT,
                        PRIMARY KEY(`id`)
                    )
                """.trimIndent())
                database.execSQL("CREATE INDEX IF NOT EXISTS `index_instruction_progress_projectId` ON `instruction_progress` (`projectId`)")
                database.execSQL("CREATE INDEX IF NOT EXISTS `index_instruction_progress_instructionSnapshotId` ON `instruction_progress` (`instructionSnapshotId`)")
                database.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_instruction_progress_projectId_instructionSnapshotId` ON `instruction_progress` (`projectId`, `instructionSnapshotId`)")

                // 10. Data migration from legacy patterns:
                // Create pattern_versions and instruction_nodes for existing patterns safely
                try {
                    val cursor = database.query("SELECT id FROM patterns")
                    val now = System.currentTimeMillis()
                    try {
                        while (cursor.moveToNext()) {
                            val patternId = cursor.getString(0)
                            val versionId = "v1_$patternId"
                            database.execSQL("""
                                INSERT OR IGNORE INTO pattern_versions (id, patternId, versionNumber, schemaVersion, status, createdAt, checksum)
                                VALUES ('$versionId', '$patternId', 1, 1, 'PUBLISHED', $now, '')
                            """)

                            // Migrate pattern steps to instruction nodes using actual column names
                            database.execSQL("""
                                INSERT OR IGNORE INTO instruction_nodes (
                                    id, sectionId, parentNodeId, orderIndex, nodeType, techniqueId, 
                                    targetLiteral, targetParamKey, countUnit, repeatTimesLiteral, 
                                    repeatTimesParamKey, displayText, measurementValue, measurementUnit, isDestructive
                                )
                                SELECT 
                                    id, 
                                    sectionId, 
                                    NULL, 
                                    orderIndex, 
                                    CASE 
                                        WHEN stepType = 'REPEAT' OR stepType = 'REPEAT_BLOCK' THEN 'REPEAT_BLOCK'
                                        WHEN stepType = 'FINISHING' OR stepType = 'JOIN' THEN 'BINARY_ACTION'
                                        ELSE 'COUNTED_ACTION'
                                    END,
                                    LOWER(stepType),
                                    stitchCount,
                                    NULL,
                                    'STITCH',
                                    repeatCount,
                                    NULL,
                                    instruction,
                                    NULL,
                                    NULL,
                                    CASE WHEN stepType = 'BIND_OFF' THEN 1 ELSE 0 END
                                FROM pattern_steps 
                                WHERE patternId = '$patternId'
                            """)
                        }
                    } finally {
                        cursor.close()
                    }
                } catch (_: Throwable) {
                    // Safety guard: continue migration cleanly even if legacy steps format varies
                }
            }
        }
    }
}
