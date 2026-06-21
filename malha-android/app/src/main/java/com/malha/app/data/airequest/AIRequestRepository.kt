package com.malha.app.data.airequest

import com.malha.app.core.database.dao.AIRequestDao
import com.malha.app.core.firebase.AuthService
import com.malha.app.core.firebase.CloudDataService
import com.malha.app.data.mapper.toDomain
import com.malha.app.data.mapper.toEntity
import com.malha.app.domain.model.AIRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.util.UUID

interface AIRequestRepository {
    fun observeAIRequests(userId: String): Flow<List<AIRequest>>
    fun observeAIRequest(requestId: String): Flow<AIRequest?>
    suspend fun createAIRequest(prompt: String): String
    suspend fun upsertLocalAIRequest(request: AIRequest)
}

class RoomAIRequestRepository(
    private val aiRequestDao: AIRequestDao
) : AIRequestRepository {
    override fun observeAIRequests(userId: String): Flow<List<AIRequest>> {
        return aiRequestDao.observeAIRequests(userId).map { list -> list.map { it.toDomain() } }
    }

    override fun observeAIRequest(requestId: String): Flow<AIRequest?> {
        return aiRequestDao.observeAIRequest(requestId).map { it?.toDomain() }
    }

    override suspend fun createAIRequest(prompt: String): String {
        val id = UUID.randomUUID().toString()
        val request = AIRequest(
            id = id,
            userId = "local_user",
            prompt = prompt,
            response = null,
            status = "pending",
            createdAt = System.currentTimeMillis()
        )
        upsertLocalAIRequest(request)
        return id
    }

    override suspend fun upsertLocalAIRequest(request: AIRequest) {
        aiRequestDao.insertAIRequest(request.toEntity())
    }
}

class CloudBackedAIRequestRepository(
    private val local: AIRequestRepository,
    private val authService: AuthService,
    private val cloudDataService: CloudDataService,
    private val syncScope: CoroutineScope
) : AIRequestRepository by local {

    override fun observeAIRequests(userId: String): Flow<List<AIRequest>> {
        syncScope.launch {
            cloudDataService.observeAIRequests(userId)
                .collectLatest { remoteRequests ->
                    remoteRequests.forEach { map ->
                        val id = map["id"] as? String ?: return@forEach
                        val uId = map["userId"] as? String ?: return@forEach
                        val prompt = map["prompt"] as? String ?: ""
                        val response = map["response"] as? String
                        val status = map["status"] as? String ?: "pending"
                        val createdAt = (map["createdAt"] as? Long) ?: System.currentTimeMillis()
                        
                        local.upsertLocalAIRequest(
                            AIRequest(id, uId, prompt, response, status, createdAt)
                        )
                    }
                }
        }
        return local.observeAIRequests(userId)
    }

    override suspend fun createAIRequest(prompt: String): String {
        val authUser = authService.currentUser
        val userId = authUser?.id ?: "local_user"
        val id = UUID.randomUUID().toString()
        val createdAt = System.currentTimeMillis()

        val request = AIRequest(id, userId, prompt, null, "pending", createdAt)
        local.upsertLocalAIRequest(request)

        if (authUser != null) {
            runCatching {
                cloudDataService.upsertAIRequest(
                    requestId = id,
                    userId = userId,
                    prompt = prompt,
                    response = null,
                    status = "pending",
                    createdAt = createdAt
                )
            }
        }
        return id
    }
}
