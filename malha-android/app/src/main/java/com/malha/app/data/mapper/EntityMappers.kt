package com.malha.app.data.mapper

import com.malha.app.core.database.entity.MaterialEntity
import com.malha.app.core.database.entity.PatternStepEntity
import com.malha.app.core.database.entity.ProjectEntity
import com.malha.app.core.database.entity.ProjectStepProgressEntity
import com.malha.app.core.database.entity.StitchPatternEntity
import com.malha.app.core.database.relation.PatternWithSteps
import com.malha.app.domain.model.CraftType
import com.malha.app.domain.model.Gauge
import com.malha.app.domain.model.Material
import com.malha.app.domain.model.MaterialType
import com.malha.app.domain.model.Pattern
import com.malha.app.domain.model.PatternSection
import com.malha.app.domain.model.PatternStep
import com.malha.app.domain.model.Project
import com.malha.app.domain.model.ProjectStepProgress
import com.malha.app.domain.model.StepType
import com.malha.app.domain.model.StitchPattern

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
        craft = try { CraftType.valueOf(pattern.craftType) } catch (e: Exception) { CraftType.KNITTING },
        difficulty = pattern.difficulty,
        gauge = if (pattern.widthStitches != null && pattern.heightRows != null) {
            Gauge(pattern.widthStitches, pattern.heightRows, pattern.measurementCm ?: 10)
        } else null,
        sections = domainSections
    )
}

fun PatternStepEntity.toDomain(): PatternStep {
    return PatternStep(
        id = id,
        orderIndex = orderIndex,
        type = try { StepType.valueOf(stepType) } catch (e: Exception) { StepType.NORMAL },
        instruction = instruction,
        rowNumber = rowNumber,
        stitchCount = stitchCount,
        confidence = confidence
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
        unit = unit
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
