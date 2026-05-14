package com.malha.app.data.mapper

import com.malha.app.core.database.entity.MaterialEntity
import com.malha.app.core.database.entity.PatternStepEntity
import com.malha.app.core.database.entity.ProjectEntity
import com.malha.app.core.database.entity.ProjectStepProgressEntity
import com.malha.app.core.database.relation.PatternWithSteps
import com.malha.app.domain.model.Material
import com.malha.app.domain.model.MaterialType
import com.malha.app.domain.model.Pattern
import com.malha.app.domain.model.PatternStep
import com.malha.app.domain.model.Project
import com.malha.app.domain.model.ProjectStepProgress

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
    return Pattern(
        id = pattern.id,
        title = pattern.title,
        steps = steps
            .sortedBy(PatternStepEntity::orderIndex)
            .map { it.toDomain() }
    )
}

fun PatternStepEntity.toDomain(): PatternStep {
    return PatternStep(
        id = id,
        orderIndex = orderIndex,
        instruction = instruction,
        rowNumber = rowNumber
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
