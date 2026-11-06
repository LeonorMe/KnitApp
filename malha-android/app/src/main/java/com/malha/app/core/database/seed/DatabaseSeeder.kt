package com.malha.app.core.database.seed

import com.malha.app.core.database.dao.MaterialDao
import com.malha.app.core.database.dao.PatternDao
import com.malha.app.core.database.dao.PatternVersionDao
import com.malha.app.core.database.dao.ProjectDao
import com.malha.app.core.database.dao.SocialDao
import com.malha.app.core.database.dao.TechniqueDao
import com.malha.app.core.database.entity.*

class DatabaseSeeder(
    private val patternDao: PatternDao,
    private val projectDao: ProjectDao,
    private val materialDao: MaterialDao,
    private val socialDao: SocialDao,
    private val techniqueDao: TechniqueDao,
    private val patternVersionDao: PatternVersionDao
) {
    suspend fun seed() {
        seedIfEmpty()
        ensureOfficialTechniques()
        ensureDemoPatternBundles()
        ensureDemoMaterials()
        ensureCompressedSquarePattern()
    }

    suspend fun seedIfEmpty() {
        if (patternDao.countPatterns() == 0) {
            patternDao.insertPatterns(SeedData.patterns)
            patternDao.insertSections(SeedData.sections)
            patternDao.insertSteps(SeedData.steps)
        }
        if (projectDao.countProjects() == 0) {
            projectDao.insertProjects(SeedData.projects)
        }
        if (materialDao.countMaterials() == 0) {
            materialDao.insertMaterials(SeedData.materials)
        }
        if (socialDao.getUser("default-user") == null) {
            for (user in SeedData.users) {
                socialDao.insertUser(user)
            }
            for (post in SeedData.posts) {
                socialDao.insertPost(post)
            }
            for (comment in SeedData.comments) {
                socialDao.insertComment(comment)
            }
        }
    }

    private suspend fun ensureOfficialTechniques() {
        if (techniqueDao.countTechniques() == 0) {
            techniqueDao.insertTechniques(OfficialTechniques.techniques)
            techniqueDao.insertAliases(OfficialTechniques.aliases)
        }
    }

    /**
     * Upserts demo patterns that are required for manual testing.
     */
    private suspend fun ensureDemoPatternBundles() {
        SeedData.patterns.forEach { pattern ->
            val exists = patternDao.countPatternsById(pattern.id) > 0
            val sectionCount = patternDao.countSectionsForPattern(pattern.id)
            val stepCount = patternDao.countStepsForPattern(pattern.id)
            if (!exists || sectionCount == 0 || stepCount == 0) {
                patternDao.insertPattern(pattern)
                val sections = SeedData.sections.filter { it.patternId == pattern.id }
                val steps = SeedData.steps.filter { it.patternId == pattern.id }
                if (sections.isNotEmpty()) {
                    patternDao.insertSections(sections)
                }
                if (steps.isNotEmpty()) {
                    patternDao.insertSteps(steps)
                }
            }
        }
    }

    private suspend fun ensureDemoMaterials() {
        SeedData.demoMaterials.forEach { material ->
            if (materialDao.countMaterialsById(material.id) == 0) {
                materialDao.insertMaterial(material)
            }
        }
    }

    /**
     * Seeds the standard 10x10 Stockinette Square compressed pattern version from the specification.
     */
    private suspend fun ensureCompressedSquarePattern() {
        val patternId = "stockinette-square"
        if (patternDao.countPatternsById(patternId) == 0) {
            val now = System.currentTimeMillis()
            val pattern = PatternEntity(
                id = patternId,
                title = "10 cm x 10 cm Stockinette Square",
                designer = "MyWool Official",
                year = 2026,
                craft = com.malha.app.domain.model.CraftType.KNITTING,
                difficulty = com.malha.app.domain.model.SkillLevel.BEGINNER,
                gaugeWidth = 20,
                gaugeHeight = 20,
                gaugeUnit = "10",
                sourceType = com.malha.app.domain.model.SourceType.MANUAL,
                originalText = "Sample square pattern from interactive pattern engine specification.",
                verificationStatus = com.malha.app.domain.model.VerificationStatus.COMMUNITY_VALIDATED,
                aiConfidence = 1.0,
                isPremium = false,
                availableSizes = "10x10",
                selectedSize = "10x10",
                updatedAt = now
            )
            patternDao.insertPattern(pattern)

            val sectionId = "sec_square_main"
            patternDao.insertSections(
                listOf(
                    PatternSectionEntity(
                        id = sectionId,
                        patternId = patternId,
                        name = "Square",
                        orderIndex = 0
                    )
                )
            )

            // Pattern version
            val versionId = "v1_$patternId"
            patternVersionDao.insertVersion(
                PatternVersionEntity(
                    id = versionId,
                    patternId = patternId,
                    versionNumber = 1,
                    schemaVersion = 1,
                    status = "PUBLISHED",
                    createdAt = now,
                    checksum = ""
                )
            )

            // Size
            patternVersionDao.insertSizes(
                listOf(
                    PatternSizeEntity(
                        id = "size_10x10",
                        patternVersionId = versionId,
                        sizeKey = "10x10",
                        displayName = "10 cm x 10 cm",
                        orderIndex = 0
                    )
                )
            )

            // Parameters
            patternVersionDao.insertParameters(
                listOf(
                    PatternParameterEntity(
                        id = "param_stitch_10x10",
                        patternVersionId = versionId,
                        sizeKey = "10x10",
                        paramKey = "STITCH_COUNT",
                        value = "20",
                        unit = "stitches"
                    ),
                    PatternParameterEntity(
                        id = "param_row_10x10",
                        patternVersionId = versionId,
                        sizeKey = "10x10",
                        paramKey = "ROW_COUNT",
                        value = "20",
                        unit = "rows"
                    )
                )
            )

            // Instruction nodes:
            // 1. Cast-on 20
            // 2. Repeat 20 rows alternating Knit 20 and Purl 20
            // 3. Bind-off 20
            val repeatNodeId = "node_rep_body"
            val nodes = listOf(
                InstructionNodeEntity(
                    id = "node_cast_on",
                    sectionId = sectionId,
                    parentNodeId = null,
                    orderIndex = 0,
                    nodeType = "COUNTED_ACTION",
                    techniqueId = "cast-on",
                    targetLiteral = null,
                    targetParamKey = "STITCH_COUNT",
                    countUnit = "STITCH",
                    displayText = "Cast on 20 stitches"
                ),
                InstructionNodeEntity(
                    id = repeatNodeId,
                    sectionId = sectionId,
                    parentNodeId = null,
                    orderIndex = 1,
                    nodeType = "REPEAT_BLOCK",
                    techniqueId = null,
                    repeatTimesLiteral = null,
                    repeatTimesParamKey = "ROW_COUNT",
                    countUnit = "ROW"
                ),
                InstructionNodeEntity(
                    id = "node_knit_row",
                    sectionId = sectionId,
                    parentNodeId = repeatNodeId,
                    orderIndex = 0,
                    nodeType = "COUNTED_ACTION",
                    techniqueId = "knit",
                    targetLiteral = null,
                    targetParamKey = "STITCH_COUNT",
                    countUnit = "STITCH",
                    displayText = "Knit 20 stitches"
                ),
                InstructionNodeEntity(
                    id = "node_purl_row",
                    sectionId = sectionId,
                    parentNodeId = repeatNodeId,
                    orderIndex = 1,
                    nodeType = "COUNTED_ACTION",
                    techniqueId = "purl",
                    targetLiteral = null,
                    targetParamKey = "STITCH_COUNT",
                    countUnit = "STITCH",
                    displayText = "Purl 20 stitches"
                ),
                InstructionNodeEntity(
                    id = "node_bind_off",
                    sectionId = sectionId,
                    parentNodeId = null,
                    orderIndex = 2,
                    nodeType = "COUNTED_ACTION",
                    techniqueId = "bind-off",
                    targetLiteral = null,
                    targetParamKey = "STITCH_COUNT",
                    countUnit = "STITCH",
                    displayText = "Bind off 20 stitches",
                    isDestructive = true
                )
            )
            patternVersionDao.insertInstructionNodes(nodes)
        }
    }
}
