package com.malha.app.domain.model

data class Project(
    val id: String,
    val name: String,
    val patternId: String?,
    val progressPercent: Int,
    val currentStepIndex: Int
)
