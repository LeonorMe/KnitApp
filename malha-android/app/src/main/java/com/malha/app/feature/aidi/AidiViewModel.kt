package com.malha.app.feature.aidi

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.malha.app.core.ai.AidiMessage
import com.malha.app.core.ai.AidiSender
import com.malha.app.core.app.appContainer
import java.util.UUID
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AidiViewModel(application: Application) : AndroidViewModel(application) {
    private val _uiState = MutableStateFlow(AidiUiState())
    val uiState: StateFlow<AidiUiState> = _uiState

    val suggestedPrompts = listOf(
        "Import pattern from text",
        "Help me plan a sweater",
        "How do I check gauge?",
        "What yarn should I use?",
        "Explain this pattern step"
    )

    fun sendMessage(text: String) {
        val prompt = text.trim()
        if (prompt.isBlank()) return

        val userMessage = AidiMessage(
            id = UUID.randomUUID().toString(),
            sender = AidiSender.User,
            text = prompt
        )

        _uiState.update {
            it.copy(
                messages = it.messages + userMessage,
                isThinking = true,
                errorMessage = null
            )
        }

        viewModelScope.launch {
            runCatching {
                if (prompt.contains("Row", ignoreCase = true) || prompt.contains("Rnd", ignoreCase = true)) {
                    // It looks like a pattern!
                    val parsed = com.malha.app.core.ai.PatternParser.parse(prompt)
                    "I've analyzed your pattern! It has ${parsed.steps.size} steps. Would you like me to save this to your Pattern Library?"
                } else {
                    appContainer.aidiAssistantService.generateReply(prompt)
                }
            }.onSuccess { reply ->
                val aidiMessage = AidiMessage(
                    id = UUID.randomUUID().toString(),
                    sender = AidiSender.Aidi,
                    text = reply
                )
                _uiState.update {
                    it.copy(
                        messages = it.messages + aidiMessage,
                        isThinking = false
                    )
                }
            }.onFailure { error ->
                _uiState.update {
                    it.copy(
                        isThinking = false,
                        errorMessage = error.message ?: "Aidi could not answer right now."
                    )
                }
            }
        }
    }
}

