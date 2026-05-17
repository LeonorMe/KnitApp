package com.malha.app.core.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val id: String,
    val name: String,
    val bio: String?,
    val profilePicUri: String?,
    val coins: Int,
    val updatedAt: Long
)

@Entity(
    tableName = "posts",
    indices = [Index("userId"), Index("patternId")]
)
data class PostEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val patternId: String?,
    val imageUri: String,
    val description: String,
    val status: String,
    val likesCount: Int = 0,
    val createdAt: Long,
    val updatedAt: Long
)

@Entity(
    tableName = "comments",
    indices = [Index("postId"), Index("userId")],
    foreignKeys = [
        ForeignKey(
            entity = PostEntity::class,
            parentColumns = ["id"],
            childColumns = ["postId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class CommentEntity(
    @PrimaryKey val id: String,
    val postId: String,
    val userId: String,
    val content: String,
    val createdAt: Long
)
