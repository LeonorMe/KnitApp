package com.malha.app.feature.aidi

import com.malha.app.core.ai.AidiMessage
import com.malha.app.core.ai.AidiSender

data class AidiUiState(
    val messages: List<AidiMessage> = listOf(
        AidiMessage(
            id = "welcome",
            sender = AidiSender.Aidi,
            text = "Hi, I am Aidi. Ask me about a pattern step, yarn choice, gauge, project planning, or how to keep a project moving."
        )
    ),
    val isThinking: Boolean = false,
    val errorMessage: String? = null
)

