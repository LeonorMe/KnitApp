package com.malha.app.data.social

import com.malha.app.core.database.dao.SocialDao
import com.malha.app.core.database.entity.CommentEntity
import com.malha.app.core.database.entity.PostEntity
import com.malha.app.core.database.entity.UserEntity
import com.malha.app.data.mapper.toPostDomain
import com.malha.app.data.mapper.toUserDomain
import com.malha.app.domain.model.Comment
import com.malha.app.domain.model.Post
import com.malha.app.domain.model.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID

class RoomSocialRepository(
    private val socialDao: SocialDao,
    private val currentUserId: String = "default-user" // Mocked for now
) : SocialRepository {

    private val likedPostIds = mutableSetOf<String>()

    override fun observeCurrentUser(): Flow<User?> {
        return socialDao.observeUser(currentUserId).map { it?.toUserDomain() }
    }

    override suspend fun getCurrentUser(): User? {
        var user = socialDao.getUser(currentUserId)?.toUserDomain()
        if (user == null) {
            // Seed initial user
            val newUser = UserEntity(
                id = currentUserId,
                name = "New Crafter",
                bio = "Exploring the world of knitting.",
                profilePicUri = null,
                coins = 100,
                updatedAt = System.currentTimeMillis()
            )
            socialDao.insertUser(newUser)
            user = newUser.toUserDomain()
        }
        return user
    }

    override suspend fun updateProfile(name: String, bio: String?, profilePicUri: String?) {
        val current = getCurrentUser() ?: return
        socialDao.insertUser(
            UserEntity(
                id = current.id,
                name = name,
                bio = bio,
                profilePicUri = profilePicUri ?: current.profilePicUri,
                coins = current.coins,
                updatedAt = System.currentTimeMillis()
            )
        )
    }

    override fun observeFeed(): Flow<List<Post>> {
        return socialDao.observeFeed().map { posts ->
            posts.map { it.toPostDomain(isLiked = likedPostIds.contains(it.post.id)) }
        }
    }

    override fun observeUserPosts(userId: String): Flow<List<Post>> {
        return socialDao.observeUserPosts(userId).map { posts ->
            posts.map { it.toPostDomain(isLiked = likedPostIds.contains(it.post.id)) }
        }
    }

    override suspend fun createPost(patternId: String?, imageUri: String, description: String, status: String) {
        val post = PostEntity(
            id = UUID.randomUUID().toString(),
            userId = currentUserId,
            patternId = patternId,
            imageUri = imageUri,
            description = description,
            status = status,
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )
        socialDao.insertPost(post)
    }

    override suspend fun toggleLikePost(postId: String) {
        val post = socialDao.getPost(postId) ?: return
        if (likedPostIds.contains(postId)) {
            likedPostIds.remove(postId)
            val newLikes = maxOf(0, post.likesCount - 1)
            socialDao.updatePostLikes(postId, newLikes)
        } else {
            likedPostIds.add(postId)
            val newLikes = post.likesCount + 1
            socialDao.updatePostLikes(postId, newLikes)
        }
    }

    override fun observeComments(postId: String): Flow<List<Comment>> {
        return socialDao.observeComments(postId).map { comments -> 
            // In a real app we'd join with User table or fetch users separately
            comments.map { 
                Comment(
                    id = it.id,
                    postId = it.postId,
                    userId = it.userId,
                    userName = "User ${it.userId.take(4)}",
                    content = it.content,
                    createdAt = it.createdAt
                )
            }
        }
    }

    override suspend fun addComment(postId: String, content: String) {
        socialDao.insertComment(
            CommentEntity(
                id = UUID.randomUUID().toString(),
                postId = postId,
                userId = currentUserId,
                content = content,
                createdAt = System.currentTimeMillis()
            )
        )
    }

    override suspend fun addCoins(amount: Int) {
        val current = getCurrentUser() ?: return
        socialDao.insertUser(
            UserEntity(
                id = current.id,
                name = current.name,
                bio = current.bio,
                profilePicUri = current.profilePicUri,
                coins = current.coins + amount,
                updatedAt = System.currentTimeMillis()
            )
        )
    }
}
