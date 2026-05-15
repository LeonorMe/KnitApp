package com.malha.app.core.ai

interface AidiAssistantService {
    suspend fun generateReply(prompt: String): String
}

