package com.malha.app.data.variant

import com.malha.app.core.database.dao.VariantDao
import com.malha.app.core.firebase.AuthService
import com.malha.app.core.firebase.CloudDataService
import com.malha.app.data.mapper.toDomain
import com.malha.app.data.mapper.toEntity
import com.malha.app.domain.model.Variant
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.util.UUID

interface VariantRepository {
    fun observeVariants(patternId: String): Flow<List<Variant>>
    suspend fun createVariant(patternId: String, name: String, notes: String?): String
    suspend fun upsertLocalVariant(variant: Variant)
}

class RoomVariantRepository(
    private val variantDao: VariantDao
) : VariantRepository {
    override fun observeVariants(patternId: String): Flow<List<Variant>> {
        return variantDao.observeVariants(patternId).map { list -> list.map { it.toDomain() } }
    }

    override suspend fun createVariant(patternId: String, name: String, notes: String?): String {
        val id = UUID.randomUUID().toString()
        val variant = Variant(
            id = id,
            patternId = patternId,
            userId = "local_user",
            name = name,
            notes = notes,
            updatedAt = System.currentTimeMillis()
        )
        upsertLocalVariant(variant)
        return id
    }

    override suspend fun upsertLocalVariant(variant: Variant) {
        variantDao.insertVariant(variant.toEntity())
    }
}

class CloudBackedVariantRepository(
    private val local: VariantRepository,
    private val authService: AuthService,
    private val cloudDataService: CloudDataService,
    private val syncScope: CoroutineScope
) : VariantRepository by local {

    override fun observeVariants(patternId: String): Flow<List<Variant>> {
        syncScope.launch {
            cloudDataService.observeVariants(patternId)
                .collectLatest { remoteVariants ->
                    remoteVariants.forEach { map ->
                        val id = map["id"] as? String ?: return@forEach
                        val patId = map["patternId"] as? String ?: return@forEach
                        val userId = map["userId"] as? String ?: return@forEach
                        val name = map["name"] as? String ?: return@forEach
                        val notes = map["notes"] as? String
                        val updatedAt = (map["updatedAt"] as? Long) ?: System.currentTimeMillis()
                        
                        local.upsertLocalVariant(
                            Variant(id, patId, userId, name, notes, updatedAt)
                        )
                    }
                }
        }
        return local.observeVariants(patternId)
    }

    override suspend fun createVariant(patternId: String, name: String, notes: String?): String {
        val authUser = authService.currentUser
        val userId = authUser?.id ?: "local_user"
        val id = UUID.randomUUID().toString()
        val updatedAt = System.currentTimeMillis()

        val variant = Variant(id, patternId, userId, name, notes, updatedAt)
        local.upsertLocalVariant(variant)

        if (authUser != null) {
            runCatching {
                cloudDataService.upsertVariant(
                    variantId = id,
                    patternId = patternId,
                    userId = userId,
                    name = name,
                    notes = notes,
                    updatedAt = updatedAt
                )
            }
        }
        return id
    }
}
