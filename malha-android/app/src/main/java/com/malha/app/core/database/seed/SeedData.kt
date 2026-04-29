package com.malha.app.core.database.seed

import com.malha.app.core.database.entity.MaterialEntity
import com.malha.app.core.database.entity.PatternEntity
import com.malha.app.core.database.entity.PatternStepEntity
import com.malha.app.core.database.entity.ProjectEntity

object SeedData {
    private const val createdAt = 1_778_000_000_000L

    val patterns = listOf(
        PatternEntity(
            id = "pattern-scarf-basic",
            title = "Starter Garter Scarf",
            description = "A calm beginner scarf for learning steady rows.",
            craftType = "knitting",
            difficulty = "beginner",
            sourceType = "included",
            verificationStatus = "peopleApproved",
            isPremium = false,
            createdAt = createdAt,
            updatedAt = createdAt
        ),
        PatternEntity(
            id = "pattern-granny-square",
            title = "First Granny Square",
            description = "A small crochet square for practicing repeats.",
            craftType = "crochet",
            difficulty = "beginner",
            sourceType = "included",
            verificationStatus = "peopleApproved",
            isPremium = false,
            createdAt = createdAt,
            updatedAt = createdAt
        ),
        PatternEntity(
            id = "pattern-ribbed-hat",
            title = "Simple Ribbed Hat",
            description = "An approachable ribbed hat with clear progress stages.",
            craftType = "knitting",
            difficulty = "intermediate",
            sourceType = "included",
            verificationStatus = "peopleApproved",
            isPremium = false,
            createdAt = createdAt,
            updatedAt = createdAt
        )
    )

    val steps = listOf(
        PatternStepEntity(
            id = "step-scarf-1",
            patternId = "pattern-scarf-basic",
            orderIndex = 0,
            rowNumber = null,
            instruction = "Cast on 28 stitches with medium-weight yarn.",
            stepType = "setup",
            repeatInfo = null,
            createdAt = createdAt,
            updatedAt = createdAt
        ),
        PatternStepEntity(
            id = "step-scarf-2",
            patternId = "pattern-scarf-basic",
            orderIndex = 1,
            rowNumber = 1,
            instruction = "Knit every stitch across the row.",
            stepType = "row",
            repeatInfo = null,
            createdAt = createdAt,
            updatedAt = createdAt
        ),
        PatternStepEntity(
            id = "step-scarf-3",
            patternId = "pattern-scarf-basic",
            orderIndex = 2,
            rowNumber = null,
            instruction = "Repeat the knit row until the scarf reaches the desired length.",
            stepType = "repeat",
            repeatInfo = "Repeat row 1",
            createdAt = createdAt,
            updatedAt = createdAt
        ),
        PatternStepEntity(
            id = "step-granny-1",
            patternId = "pattern-granny-square",
            orderIndex = 0,
            rowNumber = 1,
            instruction = "Chain 4 and slip stitch into the first chain to form a ring.",
            stepType = "setup",
            repeatInfo = null,
            createdAt = createdAt,
            updatedAt = createdAt
        ),
        PatternStepEntity(
            id = "step-granny-2",
            patternId = "pattern-granny-square",
            orderIndex = 1,
            rowNumber = 2,
            instruction = "Chain 3, work 2 double crochet into the ring, then chain 2.",
            stepType = "row",
            repeatInfo = null,
            createdAt = createdAt,
            updatedAt = createdAt
        ),
        PatternStepEntity(
            id = "step-granny-3",
            patternId = "pattern-granny-square",
            orderIndex = 2,
            rowNumber = null,
            instruction = "Repeat the cluster and chain spaces three more times, then join.",
            stepType = "repeat",
            repeatInfo = "Repeat 3 times",
            createdAt = createdAt,
            updatedAt = createdAt
        ),
        PatternStepEntity(
            id = "step-hat-1",
            patternId = "pattern-ribbed-hat",
            orderIndex = 0,
            rowNumber = null,
            instruction = "Cast on 88 stitches and join in the round, taking care not to twist.",
            stepType = "setup",
            repeatInfo = null,
            createdAt = createdAt,
            updatedAt = createdAt
        ),
        PatternStepEntity(
            id = "step-hat-2",
            patternId = "pattern-ribbed-hat",
            orderIndex = 1,
            rowNumber = null,
            instruction = "Work k2, p2 ribbing for 18 cm.",
            stepType = "repeat",
            repeatInfo = "k2, p2",
            createdAt = createdAt,
            updatedAt = createdAt
        ),
        PatternStepEntity(
            id = "step-hat-3",
            patternId = "pattern-ribbed-hat",
            orderIndex = 2,
            rowNumber = null,
            instruction = "Begin crown decreases, keeping the rib pattern aligned where possible.",
            stepType = "shaping",
            repeatInfo = null,
            createdAt = createdAt,
            updatedAt = createdAt
        )
    )

    val projects = listOf(
        ProjectEntity(
            id = "project-first-scarf",
            name = "First Calm Scarf",
            craftType = "knitting",
            patternId = "pattern-scarf-basic",
            status = "active",
            currentStepIndex = 1,
            progressPercent = 33,
            startedAt = createdAt,
            completedAt = null,
            createdAt = createdAt,
            updatedAt = createdAt
        )
    )

    val materials = listOf(
        MaterialEntity(
            id = "material-sage-yarn",
            type = "yarn",
            name = "Sage wool",
            color = "Sage",
            fiber = "Wool",
            weight = "DK",
            quantity = 2.0,
            unit = "skeins",
            lengthMeters = 400.0,
            costCents = 1200,
            purchasedAt = createdAt,
            createdAt = createdAt,
            updatedAt = createdAt
        )
    )
}

