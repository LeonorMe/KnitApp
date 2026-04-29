package com.malha.app.core.database.relation

import androidx.room.Embedded
import androidx.room.Relation
import com.malha.app.core.database.entity.PatternEntity
import com.malha.app.core.database.entity.PatternStepEntity

data class PatternWithSteps(
    @Embedded val pattern: PatternEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "patternId"
    )
    val steps: List<PatternStepEntity>
)

