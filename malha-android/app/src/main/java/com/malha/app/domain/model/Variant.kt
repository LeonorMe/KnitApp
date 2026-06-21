package com.malha.app.domain.model

import java.util.UUID

data class Variant(
    val id: String = UUID.randomUUID().toString(),
    val patternId: String,
    val userId: String,
    val name: String,
    val notes: String? = null,
    val updatedAt: Long = System.currentTimeMillis()
)
