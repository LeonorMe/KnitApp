package com.malha.app.data.pattern

import com.malha.app.core.database.dao.PatternDao
import com.malha.app.core.database.entity.PatternEntity
import com.malha.app.core.database.entity.PatternSectionEntity
import com.malha.app.core.database.entity.PatternStepEntity
import com.malha.app.data.mapper.toDomain
import com.malha.app.domain.model.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID

class RoomPatternRepository(
    private val patternDao: PatternDao
) : PatternRepository {
    override fun observePatterns(): Flow<List<Pattern>> {
        return patternDao.observePatterns()
            .map { patterns -> patterns.map { it.toDomain() } }
    }

    override fun observePattern(patternId: String): Flow<Pattern?> {
        return patternDao.observePattern(patternId)
            .map { pattern -> pattern?.toDomain() }
    }

    override suspend fun getFirstPatternId(): String? {
        return patternDao.getFirstPatternId()
    }

    override suspend fun createManualPattern(title: String, instructions: List<String>) {
        val now = System.currentTimeMillis()
        val patternId = UUID.randomUUID().toString()
        val sectionId = UUID.randomUUID().toString()
        
        patternDao.insertPattern(
            PatternEntity(
                id = patternId,
                title = title,
                designer = "Personal",
                year = java.util.Calendar.getInstance().get(java.util.Calendar.YEAR),
                craft = CraftType.KNITTING,
                difficulty = SkillLevel.BEGINNER,
                gaugeWidth = null,
                gaugeHeight = null,
                gaugeUnit = null,
                sourceType = SourceType.MANUAL,
                originalText = instructions.joinToString("\n"),
                verificationStatus = VerificationStatus.UNVERIFIED,
                aiConfidence = 1.0,
                isPremium = false,
                availableSizes = null,
                selectedSize = null,
                updatedAt = now
            )
        )
        
        patternDao.insertSection(
            PatternSectionEntity(
                id = sectionId,
                patternId = patternId,
                name = "Main",
                orderIndex = 0
            )
        )
        
        patternDao.insertSteps(
            instructions.mapIndexed { index, instruction ->
                PatternStepEntity(
                    id = UUID.randomUUID().toString(),
                    patternId = patternId,
                    sectionId = sectionId,
                    orderIndex = index,
                    stepType = StepType.NORMAL,
                    instruction = instruction,
                    rowNumber = index + 1,
                    stitchCount = null,
                    confidence = 1.0,
                    repeatCount = null,
                    everyNRows = null,
                    startRow = null,
                    endRow = null,
                    condition = null,
                    stitchPatternId = null,
                    updatedAt = now
                )
            }
        )
    }
}
