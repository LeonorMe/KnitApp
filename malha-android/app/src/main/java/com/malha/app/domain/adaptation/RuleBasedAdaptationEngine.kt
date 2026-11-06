package com.malha.app.domain.adaptation

import com.malha.app.domain.model.Material
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.roundToInt

class RuleBasedAdaptationEngine : PatternAdaptationEngine {
    override fun adapt(
        requirement: PatternRequirement,
        selectedNeedle: Material?,
        selectedYarn: Material?
    ): AdaptationResult {
        val targetNeedleMm = selectedNeedle?.sizeMm
        val targetYarnWeight = selectedYarn?.weightCategory

        val needleMismatch = targetNeedleMm != null && abs(targetNeedleMm - requirement.recommendedNeedleMm) >= 0.25
        val yarnMismatch = targetYarnWeight != null && !targetYarnWeight.equals(requirement.recommendedYarnWeight, ignoreCase = true)

        val isMismatch = needleMismatch || yarnMismatch

        if (!isMismatch) {
            return AdaptationResult(
                isMismatch = false,
                recommendedNeedleMm = requirement.recommendedNeedleMm,
                selectedNeedleMm = targetNeedleMm,
                recommendedYarnWeight = requirement.recommendedYarnWeight,
                selectedYarnWeight = targetYarnWeight,
                baseYarnMeters = requirement.baseYardageMeters,
                adjustedYarnMeters = requirement.baseYardageMeters,
                adjustmentPercent = 0,
                summary = null,
                details = emptyList()
            )
        }

        // Needle area ratio factor: 1mm change corresponds to ~15% yarn consumption delta
        val adjustmentFactor = if (targetNeedleMm != null && requirement.recommendedNeedleMm > 0) {
            targetNeedleMm / requirement.recommendedNeedleMm
        } else {
            1.0
        }

        val yarnMultiplier = adjustmentFactor.pow(1.25)
        val adjustedMeters = (requirement.baseYardageMeters * yarnMultiplier).roundToInt()
        val percent = (((adjustedMeters - requirement.baseYardageMeters).toDouble() / requirement.baseYardageMeters) * 100).roundToInt()

        val details = mutableListOf<String>()
        if (needleMismatch && targetNeedleMm != null) {
            val needleDiff = targetNeedleMm - requirement.recommendedNeedleMm
            if (needleDiff > 0) {
                details.add("Larger needle: stitches will be looser. Garment will be larger unless stitch count is adjusted.")
            } else {
                details.add("Smaller needle: stitches will be tighter. Fabric will be denser with more stitches per 10cm.")
            }
        }
        if (yarnMismatch && targetYarnWeight != null) {
            details.add("Yarn weight mismatch: selected \"$targetYarnWeight\" differs from recommended \"${requirement.recommendedYarnWeight}\".")
        }

        val formattedPercent = if (percent >= 0) "+$percent%" else "$percent%"
        val summary = when {
            needleMismatch && targetNeedleMm != null ->
                "Pattern designed for ${requirement.recommendedNeedleMm}mm needles. You selected ${targetNeedleMm}mm needles. Estimated yarn requirement adjusted to ${adjustedMeters}m ($formattedPercent)."
            yarnMismatch && targetYarnWeight != null ->
                "Selected yarn ($targetYarnWeight) differs from recommended (${requirement.recommendedYarnWeight}). Estimated yarn requirement: ${adjustedMeters}m."
            else -> null
        }

        return AdaptationResult(
            isMismatch = true,
            recommendedNeedleMm = requirement.recommendedNeedleMm,
            selectedNeedleMm = targetNeedleMm,
            recommendedYarnWeight = requirement.recommendedYarnWeight,
            selectedYarnWeight = targetYarnWeight,
            baseYarnMeters = requirement.baseYardageMeters,
            adjustedYarnMeters = adjustedMeters,
            adjustmentPercent = percent,
            summary = summary,
            details = details
        )
    }
}
