package com.malha.app.data.pattern

import com.malha.app.core.database.dao.PatternDao
import com.malha.app.core.database.entity.PatternEntity
import com.malha.app.core.database.entity.PatternStepEntity
import com.malha.app.data.mapper.toDomain
import com.malha.app.domain.model.Pattern
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
        patternDao.insertPattern(
            PatternEntity(
                id = patternId,
                title = title,
                description = "Manual pattern",
                craftType = "knitting",
                difficulty = "beginner",
                sourceType = "manual",
                verificationStatus = "personal",
                isPremium = false,
                createdAt = now,
                updatedAt = now
            )
        )
        patternDao.insertSteps(
            instructions.mapIndexed { index, instruction ->
                PatternStepEntity(
                    id = UUID.randomUUID().toString(),
                    patternId = patternId,
                    orderIndex = index,
                    rowNumber = index + 1,
                    instruction = instruction,
                    stepType = "row",
                    repeatInfo = null,
                    createdAt = now,
                    updatedAt = now
                )
            }
        )
    }
}
