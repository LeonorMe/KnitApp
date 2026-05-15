package com.malha.app.core.ai

data class AidiMessage(
    val id: String,
    val sender: AidiSender,
    val text: String,
    val createdAt: Long = System.currentTimeMillis()
)

enum class AidiSender {
    User,
    Aidi
}

