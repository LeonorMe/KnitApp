package com.malha.app.data.social

import com.malha.app.domain.model.Comment
import com.malha.app.domain.model.Post
import com.malha.app.domain.model.User
import kotlinx.coroutines.flow.Flow

interface SocialRepository {
    fun observeCurrentUser(): Flow<User?>
    suspend fun getCurrentUser(): User?
    suspend fun updateProfile(name: String, bio: String?, profilePicUri: String?)
    
    fun observeFeed(): Flow<List<Post>>
    fun observeUserPosts(userId: String): Flow<List<Post>>
    suspend fun createPost(patternId: String?, imageUri: String, description: String, status: String)
    suspend fun toggleLikePost(postId: String)
    
    fun observeComments(postId: String): Flow<List<Comment>>
    suspend fun addComment(postId: String, content: String)
    suspend fun addCoins(amount: Int)
}
