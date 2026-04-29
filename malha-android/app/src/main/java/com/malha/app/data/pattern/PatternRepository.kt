package com.malha.app.data.pattern

import com.malha.app.domain.model.Pattern

interface PatternRepository {
    suspend fun getPatterns(): List<Pattern>
}

