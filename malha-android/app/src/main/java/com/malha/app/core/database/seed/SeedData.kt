package com.malha.app.core.database.seed

import com.malha.app.core.database.entity.MaterialEntity
import com.malha.app.core.database.entity.PatternEntity
import com.malha.app.core.database.entity.PatternSectionEntity
import com.malha.app.core.database.entity.PatternStepEntity
import com.malha.app.core.database.entity.ProjectEntity
import com.malha.app.domain.model.*

object SeedData {
    private val now = System.currentTimeMillis()

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
            selectedSize = "Medium",
            updatedAt = now
        ),
        PatternEntity(
            id = "demo_beanie_001",
            title = "Simple Everyday Beanie",
            designer = "Malha Demo Pattern",
            year = 2024,
            craft = CraftType.KNITTING,
            difficulty = SkillLevel.BEGINNER,
            gaugeWidth = 14,
            gaugeHeight = 20,
            gaugeUnit = "10",
            sourceType = SourceType.MANUAL,
            originalText = "Simple beginner beanie created for Malha testing purposes.",
            verificationStatus = VerificationStatus.COMMUNITY_VALIDATED,
            aiConfidence = 1.0,
            isPremium = false,
            availableSizes = "S,M,L",
            selectedSize = "M",
            updatedAt = now
        )
    )

    val sections = listOf(
        PatternSectionEntity(
            id = "section-scarf-main",
            patternId = "pattern-scarf-basic",
            name = "Main Scarf Body",
            orderIndex = 0
        ),
        PatternSectionEntity(
            id = "section-beanie-brim",
            patternId = "demo_beanie_001",
            name = "Brim",
            orderIndex = 0
        ),
        PatternSectionEntity(
            id = "section-beanie-body",
            patternId = "demo_beanie_001",
            name = "Hat Body",
            orderIndex = 1
        ),
        PatternSectionEntity(
            id = "section-beanie-crown",
            patternId = "demo_beanie_001",
            name = "Crown Shaping",
            orderIndex = 2
        )
    )

    val steps = listOf(
        PatternStepEntity(
            id = "step-beanie-1s",
            patternId = "demo_beanie_001",
            sectionId = "section-beanie-brim",
            orderIndex = 0,
            stepType = StepType.CAST_ON,
            instruction = "Cast on 72 stitches",
            rowNumber = null,
            stitchCount = 72,
            confidence = 1.0,
            repeatCount = null,
            everyNRows = null,
            startRow = null,
            endRow = null,
            condition = "S",
            stitchPatternId = null,
            updatedAt = now
        ),
        PatternStepEntity(
            id = "step-beanie-1m",
            patternId = "demo_beanie_001",
            sectionId = "section-beanie-brim",
            orderIndex = 1,
            stepType = StepType.CAST_ON,
            instruction = "Cast on 80 stitches",
            rowNumber = null,
            stitchCount = 80,
            confidence = 1.0,
            repeatCount = null,
            everyNRows = null,
            startRow = null,
            endRow = null,
            condition = "M",
            stitchPatternId = null,
            updatedAt = now
        ),
        PatternStepEntity(
            id = "step-beanie-1l",
            patternId = "demo_beanie_001",
            sectionId = "section-beanie-brim",
            orderIndex = 2,
            stepType = StepType.CAST_ON,
            instruction = "Cast on 88 stitches",
            rowNumber = null,
            stitchCount = 88,
            confidence = 1.0,
            repeatCount = null,
            everyNRows = null,
            startRow = null,
            endRow = null,
            condition = "L",
            stitchPatternId = null,
            updatedAt = now
        ),
        PatternStepEntity(
            id = "step-beanie-2",
            patternId = "demo_beanie_001",
            sectionId = "section-beanie-brim",
            orderIndex = 3,
            stepType = StepType.NORMAL,
            instruction = "Join in the round, being careful not to twist stitches.",
            rowNumber = null,
            stitchCount = null,
            confidence = 1.0,
            repeatCount = null,
            everyNRows = null,
            startRow = null,
            endRow = null,
            condition = null,
            stitchPatternId = null,
            updatedAt = now
        ),
        PatternStepEntity(
            id = "step-beanie-3",
            patternId = "demo_beanie_001",
            sectionId = "section-beanie-brim",
            orderIndex = 4,
            stepType = StepType.REPEAT_BLOCK,
            instruction = "Work 1x1 rib for 5 cm.",
            rowNumber = null,
            stitchCount = null,
            confidence = 1.0,
            repeatCount = null,
            everyNRows = null,
            startRow = null,
            endRow = null,
            condition = null,
            stitchPatternId = "rib_1x1",
            updatedAt = now
        ),
        PatternStepEntity(
            id = "step-beanie-body-1",
            patternId = "demo_beanie_001",
            sectionId = "section-beanie-body",
            orderIndex = 5,
            stepType = StepType.REPEAT_BLOCK,
            instruction = "Knit every round until piece measures 18 cm from cast on edge.",
            rowNumber = null,
            stitchCount = null,
            confidence = 1.0,
            repeatCount = null,
            everyNRows = null,
            startRow = null,
            endRow = null,
            condition = null,
            stitchPatternId = "stockinette_round",
            updatedAt = now
        ),
        PatternStepEntity(
            id = "step-beanie-crown-1",
            patternId = "demo_beanie_001",
            sectionId = "section-beanie-crown",
            orderIndex = 6,
            stepType = StepType.DECREASE,
            instruction = "K8, k2tog around.",
            rowNumber = null,
            stitchCount = null,
            confidence = 0.98,
            repeatCount = null,
            everyNRows = null,
            startRow = null,
            endRow = null,
            condition = null,
            stitchPatternId = null,
            updatedAt = now
        ),
        PatternStepEntity(
            id = "step-beanie-crown-final",
            patternId = "demo_beanie_001",
            sectionId = "section-beanie-crown",
            orderIndex = 7,
            stepType = StepType.FINISHING,
            instruction = "Cut yarn, thread through remaining stitches, pull tight and weave in ends.",
            rowNumber = null,
            stitchCount = 8,
            confidence = 1.0,
            repeatCount = null,
            everyNRows = null,
            startRow = null,
            endRow = null,
            condition = null,
            stitchPatternId = null,
            updatedAt = now
        ),
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
            stitchPatternId = null,
            updatedAt = now
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
            stitchPatternId = null,
            updatedAt = now
        )
    )

    val projects = listOf(
        ProjectEntity(
            id = "project-first-scarf",
            name = "My First Scarf",
            imageUri = null,
            craftType = "knitting",
            patternId = "pattern-scarf-basic",
            status = "active",
            progressPercent = 10,
            currentStepIndex = 1,
            startedAt = now,
            completedAt = null,
            createdAt = now,
            updatedAt = now
        ),
        ProjectEntity(
            id = "project-demo-beanie",
            name = "Demo Beanie Project",
            imageUri = null,
            craftType = "knitting",
            patternId = "demo_beanie_001",
            status = "active",
            progressPercent = 0,
            currentStepIndex = 0,
            startedAt = now,
            completedAt = null,
            createdAt = now,
            updatedAt = now
        )
    )

    val materials = listOf(
        MaterialEntity(
            id = "material-chunky-wool",
            name = "Chunky Wool",
            imageUri = null,
            type = "yarn",
            quantity = 1.0,
            unit = "skeins",
            fiber = "100% Wool",
            gramsPerBall = 110,
            needleType = null,
            sizeMm = 6.0,
            lengthCm = null,
            updatedAt = now
        ),
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
            lengthCm = null,
            updatedAt = now
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
            lengthCm = 35,
            updatedAt = now
        )
    )
}
