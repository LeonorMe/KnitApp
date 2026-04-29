package com.malha.app.data.pattern

import com.malha.app.domain.model.Pattern
import kotlinx.coroutines.flow.Flow

interface PatternRepository {
    fun observePatterns(): Flow<List<Pattern>>
    suspend fun createManualPattern(title: String, instructions: List<String>)
}
