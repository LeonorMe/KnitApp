package com.malha.app.domain.model

data class Project(
    val id: String,
    val name: String,
    val imageUri: String?,
    val patternId: String?,
    val progressPercent: Int,
    val currentStepIndex: Int,
    val updatedAt: Long,
    val colorHex: String? = null,
    val selectedSize: String? = null,
    val status: String = "draft",
    val completedAt: Long? = null,
    val patternTitle: String? = null,
    val patternSnapshotJson: String? = null
) {
    val projectStatus: ProjectStatus
        get() = ProjectStatus.from(status)
}

