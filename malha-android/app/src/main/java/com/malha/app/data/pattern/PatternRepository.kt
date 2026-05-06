package com.malha.app.data.pattern

import com.malha.app.domain.model.Pattern
import kotlinx.coroutines.flow.Flow

interface PatternRepository {
    fun observePatterns(): Flow<List<Pattern>>
    fun observePattern(patternId: String): Flow<Pattern?>
    suspend fun getFirstPatternId(): String?
    suspend fun createManualPattern(title: String, instructions: List<String>)
}
