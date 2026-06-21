package com.malha.app.domain.model

import java.util.UUID

data class Review(
    val id: String = UUID.randomUUID().toString(),
    val patternId: String,
    val userId: String,
    val rating: Int,
    val text: String,
    val createdAt: Long = System.currentTimeMillis()
)
