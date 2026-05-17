package com.malha.app.data.mapper

import com.malha.app.core.database.entity.*
import com.malha.app.core.database.relation.PatternWithSteps
import com.malha.app.core.database.relation.PostWithDetails
import com.malha.app.domain.model.*

fun ProjectEntity.toDomain(): Project {
    return Project(
        id = id,
        name = name,
        imageUri = imageUri,
        patternId = patternId,
        progressPercent = progressPercent,
        currentStepIndex = currentStepIndex,
        updatedAt = updatedAt
    )
}

fun PatternWithSteps.toDomain(): Pattern {
    val domainSections = sections
        .sortedBy { it.orderIndex }
        .map { sectionEntity ->
            PatternSection(
                id = sectionEntity.id,
                name = sectionEntity.name,
                steps = steps
                    .filter { it.sectionId == sectionEntity.id }
                    .sortedBy { it.orderIndex }
                    .map { it.toStepDomain() }
            )
        }

    return Pattern(
        id = pattern.id,
        title = pattern.title,
        designer = pattern.designer,
        year = pattern.year,
        craft = pattern.craft,
        difficulty = pattern.difficulty,
        gauge = if (pattern.gaugeWidth != null && pattern.gaugeHeight != null) {
            Gauge(
                widthStitches = pattern.gaugeWidth,
                heightRows = pattern.gaugeHeight,
                measurementCm = pattern.gaugeUnit
                    ?.filter { it.isDigit() }
                    ?.toIntOrNull()
                    ?: 10
            )
        } else {
            null
        },
        sourceType = pattern.sourceType,
        originalText = pattern.originalText,
        verificationStatus = pattern.verificationStatus,
        aiConfidence = pattern.aiConfidence,
        isPremium = pattern.isPremium,
        availableSizes = pattern.availableSizes
            ?.split(",")
            ?.map { it.trim() }
            ?.filter { it.isNotEmpty() }
            ?: emptyList(),
        selectedSize = pattern.selectedSize,
        sections = domainSections
    )
}

fun PatternStepEntity.toStepDomain(): PatternStep {
    return PatternStep(
        id = id,
        orderIndex = orderIndex,
        type = stepType,
        instructionData = instruction,
        voiceInstruction = null, // To be implemented in JSON execution graph
        rowNumber = rowNumber,
        stitchCountData = stitchCount?.toString(),
        confidence = confidence,
        repeatLogicJson = null, // To be implemented in JSON execution graph
        estimatedRounds = null, // To be implemented in JSON execution graph
        condition = condition,
        stitchPatternId = stitchPatternId
    )
}

fun MaterialEntity.toDomain(): Material {
    return Material(
        id = id,
        name = name,
        imageUri = imageUri,
        type = MaterialType.fromStorageValue(type),
        quantity = quantity,
        unit = unit,
        fiber = fiber,
        gramsPerBall = gramsPerBall,
        needleType = needleType,
        sizeMm = sizeMm,
        lengthCm = lengthCm
    )
}

fun StitchPatternEntity.toDomain(): StitchPattern {
    return StitchPattern(
        id = id,
        name = name,
        description = description,
        instructions = instructions,
        imageUrl = imageUrl,
        videoUrl = videoUrl,
        terms = searchTerms
            .split(",")
            .map { it.trim() }
            .filter { it.isNotEmpty() }
    )
}

fun ProjectStepProgressEntity.toDomain(): ProjectStepProgress {
    return ProjectStepProgress(
        id = id,
        projectId = projectId,
        patternStepId = patternStepId,
        isDone = isDone,
        note = note
    )
}

fun UserEntity.toUserDomain(): User {
    return User(
        id = id,
        name = name,
        bio = bio,
        profilePicUri = profilePicUri,
        coins = coins
    )
}

fun PostWithDetails.toPostDomain(): Post {
    return Post(
        id = post.id,
        userId = post.userId,
        userName = user.name,
        userProfilePic = user.profilePicUri,
        patternId = post.patternId,
        patternName = pattern?.title,
        imageUri = post.imageUri,
        description = post.description,
        status = post.status,
        createdAt = post.createdAt
    )
}
