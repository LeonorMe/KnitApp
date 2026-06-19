package com.malha.app.data.social

import com.malha.app.core.firebase.AuthService
import com.malha.app.core.firebase.CloudDataService
import com.malha.app.data.pattern.PatternRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import java.util.UUID

class CloudBackedSocialRepository(
    private val local: SocialRepository,
    private val authService: AuthService,
    private val cloudDataService: CloudDataService,
    private val patternRepository: PatternRepository,
    syncScope: CoroutineScope
) : SocialRepository by local {

    init {
        // Continuous Firestore → Room sync
        cloudDataService.observePosts()
            .onEach { remotePosts ->
                remotePosts.forEach { post ->
                    // 1. Ensure author exists in users table (so JOIN works)
                    local.upsertLocalUserStub(
                        userId = post.userId,
                        name = post.userName,
                        profilePicUri = post.userProfilePic
                    )
                    // 2. Upsert the post itself
                    local.upsertLocalPost(
                        postId = post.id,
                        userId = post.userId,
                        patternId = post.patternId,
                        imageUri = post.imageUri,    // already cloud URL
                        description = post.description,
                        status = post.status,
                        createdAt = post.createdAt
                    )
                }
            }
            .launchIn(syncScope)
    }

    override suspend fun createPost(
        patternId: String?,
        imageUri: String,
        description: String,
        status: String
    ) {
        val authUser = authService.currentUser ?: run {
            local.createPost(patternId, imageUri, description, status)
            return
        }

        // Single ID, generated once
        val postId = UUID.randomUUID().toString()
        val createdAt = System.currentTimeMillis()

        // Get the user's current display info from Room (for denormalization)
        val currentUser = local.getCurrentUser()
        val userName = currentUser?.name ?: authUser.displayName ?: "Crafter"
        val userProfilePic = currentUser?.profilePicUri ?: authUser.photoUrl

        // 1. Optimistic local insert
        local.upsertLocalPost(
            postId = postId,
            userId = authUser.id,
            patternId = patternId,
            imageUri = imageUri,
            description = description,
            status = status,
            createdAt = createdAt
        )

        // 2. Cloud upload (Storage + Firestore) using the SAME postId
        runCatching {
            val patternName: String? = patternId?.let { patternRepository.getPattern(it)?.title }

            val remoteUrl = cloudDataService.createPost(
                userId = authUser.id,
                userName = userName,
                userProfilePic = userProfilePic,
                postId = postId,
                patternId = patternId,
                patternName = patternName,
                localImageUri = imageUri,
                description = description,
                status = status,
                createdAt = createdAt
            )

            // 3. Replace local URI with remote URL
            local.upsertLocalPost(
                postId = postId,
                userId = authUser.id,
                patternId = patternId,
                imageUri = remoteUrl,
                description = description,
                status = status,
                createdAt = createdAt
            )
        }
    }
}