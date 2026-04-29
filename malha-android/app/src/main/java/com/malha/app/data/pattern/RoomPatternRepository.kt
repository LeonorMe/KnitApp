package com.malha.app.data.pattern

import com.malha.app.core.database.dao.PatternDao
import com.malha.app.data.mapper.toDomain
import com.malha.app.domain.model.Pattern
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class RoomPatternRepository(
    private val patternDao: PatternDao
) : PatternRepository {
    override fun observePatterns(): Flow<List<Pattern>> {
        return patternDao.observePatterns()
            .map { patterns -> patterns.map { it.toDomain() } }
    }
}

