package com.malha.app.core.database.seed

import com.malha.app.core.database.entity.MaterialEntity
import com.malha.app.core.database.entity.PatternEntity
import com.malha.app.core.database.entity.PatternSectionEntity
import com.malha.app.core.database.entity.PatternStepEntity
import com.malha.app.core.database.entity.ProjectEntity
import com.malha.app.domain.model.*

object SeedData {
    val patterns = listOf(
        PatternEntity(
            id = "pattern-scarf-basic",
            title = "Starter Garter Scarf",
            designer = "Malha Team",
            year = 2024,
            craft = CraftType.KNITTING,
            difficulty = SkillLevel.BEGINNER,
            gaugeWidth = 20,
            gaugeHeight = 28,
            gaugeUnit = "10",
            sourceType = SourceType.MANUAL,
            originalText = "Basic garter scarf instructions...",
            verificationStatus = VerificationStatus.HUMAN_REVIEWED,
            aiConfidence = 1.0,
            isPremium = false,
            availableSizes = "Small,Medium,Large",
            selectedSize = "Medium"
        )
    )

    val sections = listOf(
        PatternSectionEntity(
            id = "section-scarf-main",
            patternId = "pattern-scarf-basic",
            name = "Main Scarf Body",
            orderIndex = 0
        )
    )

    val steps = listOf(
        PatternStepEntity(
            id = "step-scarf-1",
            patternId = "pattern-scarf-basic",
            sectionId = "section-scarf-main",
            orderIndex = 0,
            stepType = StepType.CAST_ON,
            instruction = "Cast on 30 stitches for Small size, 40 for Medium, 50 for Large.",
            rowNumber = null,
            stitchCount = 40,
            confidence = 1.0,
            repeatCount = null,
            everyNRows = null,
            startRow = null,
            endRow = null,
            condition = "Medium",
            stitchPatternId = null
        ),
        PatternStepEntity(
            id = "step-scarf-2",
            patternId = "pattern-scarf-basic",
            sectionId = "section-scarf-main",
            orderIndex = 1,
            stepType = StepType.REPEAT_BLOCK,
            instruction = "Knit every row for 150 cm.",
            rowNumber = 1,
            stitchCount = 40,
            confidence = 1.0,
            repeatCount = 150,
            everyNRows = 1,
            startRow = 1,
            endRow = 1,
            condition = null,
            stitchPatternId = null
        )
    )

    val projects = listOf(
        ProjectEntity(
            id = "project-first-scarf",
            name = "My First Scarf",
            imageUri = null,
            patternId = "pattern-scarf-basic",
            progressPercent = 10,
            currentStepIndex = 1,
            updatedAt = System.currentTimeMillis()
        )
    )

    val materials = listOf(
        MaterialEntity(
            id = "material-sage-yarn",
            name = "Sage wool",
            imageUri = null,
            type = "yarn",
            quantity = 2.0,
            unit = "skeins",
            fiber = "Wool",
            gramsPerBall = 100,
            needleType = null,
            sizeMm = null,
            lengthCm = null
        ),
        MaterialEntity(
            id = "material-bamboo-needles",
            name = "Bamboo needles 4 mm",
            imageUri = null,
            type = "needle",
            quantity = 1.0,
            unit = "pairs",
            fiber = null,
            gramsPerBall = null,
            needleType = "straight",
            sizeMm = 4.0,
            lengthCm = 35
        )
    )
}
