package com.malha.app.domain.model

data class StitchPattern(
    val id: String,
    val name: String,
    val description: String,
    val instructions: String,
    val imageUrl: String? = null,
    val videoUrl: String? = null,
    val terms: List<String> = emptyList() // Searchable terms like "K2tog", "Ssk"
)
