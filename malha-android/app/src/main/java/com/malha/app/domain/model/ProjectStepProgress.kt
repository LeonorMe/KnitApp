package com.malha.app.domain.model

data class ProjectStepProgress(
    val id: String,
    val projectId: String,
    val patternStepId: String,
    val isDone: Boolean,
    val note: String?
)

