package com.malha.app.domain.adaptation

import com.malha.app.domain.model.Pattern

object PatternRequirementExtractor {
    fun extract(pattern: Pattern, selectedSize: String? = null): PatternRequirement {
        // 1. Determine needle size:
        val needleMm = when {
            pattern.gauge != null -> when {
                pattern.gauge.widthStitches <= 12 -> 8.0
                pattern.gauge.widthStitches <= 15 -> 6.0
                pattern.gauge.widthStitches <= 18 -> 5.0
                pattern.gauge.widthStitches <= 22 -> 4.0
                pattern.gauge.widthStitches <= 26 -> 3.5
                pattern.gauge.widthStitches <= 30 -> 3.0
                else -> 2.5
            }
            pattern.originalText?.contains("6mm", ignoreCase = true) == true -> 6.0
            pattern.originalText?.contains("5mm", ignoreCase = true) == true -> 5.0
            pattern.originalText?.contains("4mm", ignoreCase = true) == true -> 4.0
            pattern.originalText?.contains("3mm", ignoreCase = true) == true -> 3.0
            else -> 4.0
        }

        // 2. Determine needle type:
        val needleType = when {
            pattern.originalText?.contains("circular", ignoreCase = true) == true -> "Circular"
            pattern.originalText?.contains("double-pointed", ignoreCase = true) == true ||
            pattern.originalText?.contains("round", ignoreCase = true) == true -> "Double Pointed"
            else -> "Straight"
        }

        // 3. Determine yarn weight category:
        val yarnWeight = when {
            needleMm >= 7.0 -> "super bulky 6"
            needleMm >= 5.5 -> "bulky 5"
            needleMm >= 4.0 -> "medium 4"
            needleMm >= 3.5 -> "light 3"
            needleMm >= 2.75 -> "fine 2"
            else -> "super fine 1"
        }

        // 4. Base yardage based on pattern and size:
        val sizeFactor = when (selectedSize?.trim()?.uppercase()) {
            "XS" -> 0.85
            "S", "SMALL" -> 0.9
            "M", "MEDIUM" -> 1.0
            "L", "LARGE" -> 1.2
            "XL" -> 1.35
            else -> 1.0
        }

        val baseMeters = when {
            pattern.title.contains("Scarf", ignoreCase = true) -> (200 * sizeFactor).toInt()
            pattern.title.contains("Beanie", ignoreCase = true) || pattern.title.contains("Hat", ignoreCase = true) -> (120 * sizeFactor).toInt()
            pattern.title.contains("Square", ignoreCase = true) -> 50
            pattern.title.contains("Sweater", ignoreCase = true) -> (900 * sizeFactor).toInt()
            else -> (150 * sizeFactor).toInt()
        }

        return PatternRequirement(
            recommendedNeedleMm = needleMm,
            recommendedNeedleType = needleType,
            recommendedYarnWeight = yarnWeight,
            recommendedYarnFiber = "100% Wool / Acrylic",
            baseYardageMeters = baseMeters
        )
    }
}
