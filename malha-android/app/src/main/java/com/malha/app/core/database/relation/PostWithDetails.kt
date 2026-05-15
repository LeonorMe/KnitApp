package com.malha.app.core.database.relation

import androidx.room.Embedded
import androidx.room.Relation
import com.malha.app.core.database.entity.PatternEntity
import com.malha.app.core.database.entity.PostEntity
import com.malha.app.core.database.entity.UserEntity

data class PostWithDetails(
    @Embedded val post: PostEntity,
    @Relation(
        parentColumn = "userId",
        entityColumn = "id"
    )
    val user: UserEntity,
    @Relation(
        parentColumn = "patternId",
        entityColumn = "id"
    )
    val pattern: PatternEntity?
)
