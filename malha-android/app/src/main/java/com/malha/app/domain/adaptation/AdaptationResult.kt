package com.malha.app.domain.adaptation

data class AdaptationResult(
    val isMismatch: Boolean,
    val recommendedNeedleMm: Double,
    val selectedNeedleMm: Double?,
    val recommendedYarnWeight: String,
    val selectedYarnWeight: String?,
    val baseYarnMeters: Int,
    val adjustedYarnMeters: Int,
    val adjustmentPercent: Int,
    val summary: String?,
    val details: List<String> = emptyList()
)
