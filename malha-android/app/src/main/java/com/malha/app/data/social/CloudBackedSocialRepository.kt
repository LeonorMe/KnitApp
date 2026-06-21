package com.malha.app.data.social

import com.malha.app.core.database.dao.SocialDao
import com.malha.app.core.database.entity.CommentEntity
import com.malha.app.core.firebase.AuthService
import com.malha.app.core.firebase.CloudDataService
import com.malha.app.core.util.SocialDefaults.DEFAULT_BIO
import com.malha.app.data.pattern.PatternRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import java.util.UUID
import com.malha.app.core.util.SocialDefaults.INITIAL_COINS
import com.malha.app.core.util.SocialDefaults.DEFAULT_DISPLAY_NAME
import com.malha.app.domain.model.Comment
import com.malha.app.domain.model.User
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class CloudBackedSocialRepository(
    private val local: SocialRepository,
    private val socialDao: SocialDao,
    private val authService: AuthService,
    private val cloudDataService: CloudDataService,
    private val patternRepository: PatternRepository,
    private val syncScope: CoroutineScope
) : SocialRepository by local {

    private var syncJob: Job? = null

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
        startUserSync()
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

        val postId = UUID.randomUUID().toString()
        val createdAt = System.currentTimeMillis()

        val currentUser = local.getCurrentUser()
        val userName = currentUser?.name ?: authUser.displayName ?: "Crafter"
        val userProfilePic = currentUser?.profilePicUri ?: authUser.photoUrl

        local.upsertLocalPost(
            postId = postId,
            userId = authUser.id,
            patternId = patternId,
            imageUri = imageUri,
            description = description,
            status = status,
            createdAt = createdAt
        )

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

    private fun startUserSync() {
        syncJob?.cancel()
        syncJob = syncScope.launch {
            authService.authState
                .flatMapLatest { authUser ->
                    val userId = authUser?.id
                    if (userId != null) {
                        cloudDataService.observeUserProfile(userId)
                    } else {
                        kotlinx.coroutines.flow.flowOf(null)
                    }
                }
                .collectLatest { data ->
                    if (data != null) {
                        val user = data.toUserDomain()
                        if (user != null) {
                            local.cacheUser(user)
                        }
                    }
                }
        }
    }

    private fun Map<String, Any?>.toUserDomain(): User? {
        val id = this["id"] as? String ?: return null
        return User(
            id = id,
            name = (this["displayName"] as? String) ?: DEFAULT_DISPLAY_NAME,
            bio = (this["bio"] as? String) ?: DEFAULT_BIO,
            profilePicUri = this["profilePhotoUri"] as? String,
            coins = (this["coins"] as? Long)?.toInt() ?: INITIAL_COINS
        )
    }

    override fun observeComments(postId: String): Flow<List<Comment>> {
        syncScope.launch {
            cloudDataService.observeComments(postId)
                .collectLatest { remoteComments ->
                    remoteComments.forEach { map ->
                        val id = map["id"] as? String ?: return@forEach
                        val pId = map["postId"] as? String ?: return@forEach
                        val uId = map["userId"] as? String ?: return@forEach
                        val content = map["content"] as? String ?: ""
                        val createdAt = (map["createdAt"] as? Long) ?: System.currentTimeMillis()

                        val uName = map["userName"] as? String ?: "User ${uId.take(4)}"
                        val uPic = map["userProfilePic"] as? String
                        local.upsertLocalUserStub(uId, uName, uPic)

                        socialDao.insertComment(
                            CommentEntity(
                                id = id,
                                postId = pId,
                                userId = uId,
                                content = content,
                                createdAt = createdAt
                            )
                        )
                    }
                }
        }
        return local.observeComments(postId)
    }

    override suspend fun addComment(postId: String, content: String) {
        val authUser = authService.currentUser
        val userId = authUser?.id ?: "local_user"
        val commentId = UUID.randomUUID().toString()
        val createdAt = System.currentTimeMillis()

        local.addComment(postId, content)

        if (authUser != null) {
            runCatching {
                cloudDataService.upsertComment(
                    commentId = commentId,
                    postId = postId,
                    userId = userId,
                    content = content,
                    createdAt = createdAt
                )
            }
        }
    }

    override suspend fun addCoins(amount: Int) {
        val userId = authService.currentUser?.id ?: return
        local.addCoins(amount)
        runCatching {
            cloudDataService.updateUserCoins(userId, amount)
        }
    }

    override suspend fun updateProfile(
        name: String,
        bio: String?,
        profilePicUri: String?
    ) {
        val userId = authService.currentUser?.id ?: return
        val current = local.getCurrentUser()
        local.updateProfile(name, bio, profilePicUri)
        runCatching {
            cloudDataService.updateUserProfile(
                userId = userId,
                username = name,
                bio = bio,
                profilePhotoUri = profilePicUri,
                coins = current?.coins ?: INITIAL_COINS
            )
        }
    }
}