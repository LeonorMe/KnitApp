package com.malha.app.data.mapper

import com.malha.app.core.database.entity.MaterialEntity
import com.malha.app.core.database.entity.PatternStepEntity
import com.malha.app.core.database.entity.ProjectEntity
import com.malha.app.core.database.entity.ProjectStepProgressEntity
import com.malha.app.core.database.entity.StitchPatternEntity
import com.malha.app.core.database.relation.PatternWithSteps
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
                    .map { it.toDomain() }
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
            Gauge(pattern.gaugeWidth, pattern.gaugeHeight, pattern.gaugeUnit?.toIntOrNull() ?: 10)
        } else null,
        sections = domainSections,
        sourceType = pattern.sourceType,
        originalText = pattern.originalText,
        verificationStatus = pattern.verificationStatus,
        aiConfidence = pattern.aiConfidence,
        isPremium = pattern.isPremium,
        availableSizes = pattern.availableSizes?.split(",") ?: emptyList(),
        selectedSize = pattern.selectedSize
    )
}

fun PatternStepEntity.toDomain(): PatternStep {
    return PatternStep(
        id = id,
        orderIndex = orderIndex,
        type = stepType,
        instruction = instruction,
        rowNumber = rowNumber,
        stitchCount = stitchCount,
        confidence = confidence,
        repeatCount = repeatCount,
        everyNRows = everyNRows,
        startRow = startRow,
        endRow = endRow,
        condition = condition,
        stitchPatternId = stitchPatternId
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
        terms = searchTerms.split(",").map { it.trim() }.filter { it.isNotEmpty() }
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

fun ProjectStepProgressEntity.toDomain(): ProjectStepProgress {
    return ProjectStepProgress(
        id = id,
        projectId = projectId,
        patternStepId = patternStepId,
        isDone = isDone,
        note = note
    )
}

fun UserEntity.toDomain(): User {
    return User(
        id = id,
        name = name,
        bio = bio,
        profilePicUri = profilePicUri,
        coins = coins
    )
}

fun PostWithDetails.toDomain(): Post {
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
