package com.malha.app.domain.adaptation

data class PatternRequirement(
    val recommendedNeedleMm: Double,
    val recommendedNeedleType: String,
    val recommendedYarnWeight: String,
    val recommendedYarnFiber: String? = null,
    val baseYardageMeters: Int = 100
)
