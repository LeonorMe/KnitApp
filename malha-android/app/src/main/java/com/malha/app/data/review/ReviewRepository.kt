package com.malha.app.data.review

import com.malha.app.core.database.dao.ReviewDao
import com.malha.app.core.firebase.AuthService
import com.malha.app.core.firebase.CloudDataService
import com.malha.app.data.mapper.toDomain
import com.malha.app.data.mapper.toEntity
import com.malha.app.domain.model.Review
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.util.UUID

interface ReviewRepository {
    fun observeReviews(patternId: String): Flow<List<Review>>
    suspend fun addReview(patternId: String, rating: Int, text: String): String
    suspend fun upsertLocalReview(review: Review)
}

class RoomReviewRepository(
    private val reviewDao: ReviewDao
) : ReviewRepository {
    override fun observeReviews(patternId: String): Flow<List<Review>> {
        return reviewDao.observeReviews(patternId).map { list -> list.map { it.toDomain() } }
    }

    override suspend fun addReview(patternId: String, rating: Int, text: String): String {
        val id = UUID.randomUUID().toString()
        val review = Review(
            id = id,
            patternId = patternId,
            userId = "local_user",
            rating = rating,
            text = text,
            createdAt = System.currentTimeMillis()
        )
        upsertLocalReview(review)
        return id
    }

    override suspend fun upsertLocalReview(review: Review) {
        reviewDao.insertReview(review.toEntity())
    }
}

class CloudBackedReviewRepository(
    private val local: ReviewRepository,
    private val authService: AuthService,
    private val cloudDataService: CloudDataService,
    private val syncScope: CoroutineScope
) : ReviewRepository by local {

    override fun observeReviews(patternId: String): Flow<List<Review>> {
        syncScope.launch {
            cloudDataService.observeReviews(patternId)
                .collectLatest { remoteReviews ->
                    remoteReviews.forEach { map ->
                        val id = map["id"] as? String ?: return@forEach
                        val patId = map["patternId"] as? String ?: return@forEach
                        val userId = map["userId"] as? String ?: return@forEach
                        val rating = (map["rating"] as? Long)?.toInt() ?: 5
                        val text = map["text"] as? String ?: ""
                        val createdAt = (map["createdAt"] as? Long) ?: System.currentTimeMillis()
                        
                        local.upsertLocalReview(
                            Review(id, patId, userId, rating, text, createdAt)
                        )
                    }
                }
        }
        return local.observeReviews(patternId)
    }

    override suspend fun addReview(patternId: String, rating: Int, text: String): String {
        val authUser = authService.currentUser
        val userId = authUser?.id ?: "local_user"
        val id = UUID.randomUUID().toString()
        val createdAt = System.currentTimeMillis()

        val review = Review(id, patternId, userId, rating, text, createdAt)
        local.upsertLocalReview(review)

        if (authUser != null) {
            runCatching {
                cloudDataService.upsertReview(
                    reviewId = id,
                    patternId = patternId,
                    userId = userId,
                    rating = rating,
                    text = text,
                    createdAt = createdAt
                )
            }
        }
        return id
    }
}
