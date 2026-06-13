package com.malha.app.feature.aidi

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.malha.app.core.ai.AidiMessage
import com.malha.app.core.ai.AidiSender
import com.malha.app.core.ai.PatternParser
import com.malha.app.core.app.appContainer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

class AidiViewModel(application: Application) : AndroidViewModel(application) {
    private val _uiState = MutableStateFlow(AidiUiState())
    val uiState: StateFlow<AidiUiState> = _uiState

    val suggestedPrompts = listOf(
        "How do I check gauge?",
        "Help me plan a sweater",
        "What yarn should I use?",
        "Explain this pattern step",
        "Import pattern from text"
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
                // Detect if the user pasted raw pattern text — parse locally for speed
                val looksLikePattern = prompt.contains("Row", ignoreCase = true) ||
                        prompt.contains("Rnd", ignoreCase = true) ||
                        prompt.contains("Cast on", ignoreCase = true)

                if (looksLikePattern && prompt.length > 60) {
                    val parsed = PatternParser.parse(prompt)
                    "I've analysed your pattern. It has ${parsed.allSteps.size} steps across " +
                            "${parsed.sections.size} section(s). Would you like me to save this " +
                            "to your Pattern Library?"
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
