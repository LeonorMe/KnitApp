package com.malha.app.domain.model

import java.util.UUID

data class AIRequest(
    val id: String = UUID.randomUUID().toString(),
    val userId: String,
    val prompt: String,
    val response: String? = null,
    val status: String = "pending", // pending, completed, failed
    val createdAt: Long = System.currentTimeMillis()
)
