package com.malha.app.data.material

import android.util.Log
import com.malha.app.core.database.dao.MaterialDao
import com.malha.app.core.database.entity.MaterialEntity
import com.malha.app.core.firebase.AuthService
import com.malha.app.core.firebase.CloudDataService
import com.malha.app.domain.model.Material
import com.malha.app.domain.model.MaterialType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch

private const val TAG = "CloudMaterialRepo"

@OptIn(ExperimentalCoroutinesApi::class)
class CloudBackedMaterialRepository(
    private val local: MaterialRepository,
    private val materialDao: MaterialDao,
    private val authService: AuthService,
    private val cloudDataService: CloudDataService,
    private val syncScope: CoroutineScope
) : MaterialRepository {

    private var syncJob: Job? = null

    init {
        startMaterialSync()
    }

    private fun startMaterialSync() {
        syncJob?.cancel()
        syncJob = syncScope.launch {
            runCatching {
                authService.authState
                    .flatMapLatest { authUser ->
                        val userId = authUser?.id
                        if (userId != null) {
                            cloudDataService.observeMaterials(userId)
                        } else {
                            flowOf(emptyList())
                        }
                    }
                    .collectLatest { remoteMaterials ->
                        remoteMaterials.forEach { map ->
                            val id = map["id"] as? String ?: return@forEach
                            val name = map["name"] as? String ?: ""
                            val typeStr = map["type"] as? String ?: "yarn"
                            val quantity = (map["quantity"] as? Double) ?: 0.0
                            val unit = map["unit"] as? String ?: ""
                            val imageUri = map["imageUri"] as? String
                            val updatedAt = (map["updatedAt"] as? Long) ?: System.currentTimeMillis()

                            runCatching {
                                materialDao.insertMaterial(
                                    MaterialEntity(
                                        id = id,
                                        name = name,
                                        imageUri = imageUri,
                                        type = typeStr,
                                        quantity = quantity,
                                        unit = unit,
                                        fiber = null,
                                        gramsPerBall = null,
                                        needleType = null,
                                        sizeMm = null,
                                        lengthCm = null,
                                        updatedAt = updatedAt
                                    )
                                )
                            }.onFailure { e -> android.util.Log.w(TAG, "Failed to insert remote material $id", e) }
                        }
                    }
            }.onFailure { e -> android.util.Log.e(TAG, "Material sync coroutine failed", e) }
        }
    }

    override fun observeMaterials(): Flow<List<Material>> = local.observeMaterials()

    override suspend fun createMaterial(
        name: String,
        type: MaterialType,
        quantity: Double,
        unit: String,
        imageUri: String?
    ): String {
        val materialId = local.createMaterial(name, type, quantity, unit, imageUri)
        val user = authService.currentUser
        Log.d(TAG, "createMaterial: id=$materialId user=${user?.id}")
        if (user != null) {
            try {
                cloudDataService.upsertMaterial(
                    userId = user.id,
                    materialId = materialId,
                    name = name,
                    type = type,
                    quantity = quantity,
                    unit = unit,
                    imageUri = imageUri
                )
                Log.d(TAG, "createMaterial: Firestore write OK for $materialId")
            } catch (e: Exception) {
                Log.e(TAG, "createMaterial: Firestore write FAILED for $materialId", e)
            }
        } else {
            Log.w(TAG, "createMaterial: no authenticated user, skipping Firestore write")
        }
        return materialId
    }

    override suspend fun updateMaterial(
        id: String,
        name: String,
        type: MaterialType,
        quantity: Double,
        unit: String,
        imageUri: String?
    ) {
        local.updateMaterial(id, name, type, quantity, unit, imageUri)
        val user = authService.currentUser
        if (user != null) {
            try {
                cloudDataService.upsertMaterial(
                    userId = user.id,
                    materialId = id,
                    name = name,
                    type = type,
                    quantity = quantity,
                    unit = unit,
                    imageUri = imageUri
                )
            } catch (e: Exception) {
                Log.e(TAG, "updateMaterial: Firestore write FAILED for $id", e)
            }
        }
    }

    override suspend fun deleteMaterial(id: String) {
        local.deleteMaterial(id)
        val user = authService.currentUser
        if (user != null) {
            try {
                cloudDataService.deleteMaterial(userId = user.id, materialId = id)
            } catch (e: Exception) {
                Log.e(TAG, "deleteMaterial: Firestore delete FAILED for $id", e)
            }
        }
    }
}
