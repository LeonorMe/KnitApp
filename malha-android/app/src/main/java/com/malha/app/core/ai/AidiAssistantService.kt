package com.malha.app.core.ai

import com.malha.app.domain.model.Pattern

interface AidiAssistantService {
    suspend fun generateReply(prompt: String): String
    suspend fun parsePattern(text: String): Pattern
}
