package com.malha.app.domain.model

import java.util.UUID

data class User(
    val id: String,
    val name: String,
    val bio: String? = null,
    val profilePicUri: String? = null,
    val coins: Int = 0
)

data class Post(
    val id: String = UUID.randomUUID().toString(),
    val userId: String,
    val userName: String,
    val userProfilePic: String? = null,
    val patternId: String? = null,
    val patternName: String? = null,
    val imageUri: String,
    val description: String,
    val status: String = "ongoing", // ongoing, finished
    val createdAt: Long = System.currentTimeMillis(),
    val likesCount: Int = 0
)

data class Comment(
    val id: String = UUID.randomUUID().toString(),
    val postId: String,
    val userId: String,
    val userName: String,
    val userProfilePic: String? = null,
    val content: String,
    val createdAt: Long = System.currentTimeMillis()
)
