package com.malha.app.domain.model

data class Pattern(
    val id: String,
    val title: String,
    val steps: List<PatternStep>
)

data class PatternStep(
    val id: String,
    val orderIndex: Int,
    val instruction: String,
    val rowNumber: Int? = null
)

