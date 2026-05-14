package com.malha.app.data.material

import com.malha.app.core.database.dao.MaterialDao
import com.malha.app.core.database.entity.MaterialEntity
import com.malha.app.data.mapper.toDomain
import com.malha.app.domain.model.Material
import com.malha.app.domain.model.MaterialType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID

class RoomMaterialRepository(
    private val materialDao: MaterialDao
) : MaterialRepository {
    override fun observeMaterials(): Flow<List<Material>> {
        return materialDao.observeMaterials()
            .map { materials -> materials.map { it.toDomain() } }
    }

    override suspend fun createMaterial(
        name: String,
        type: MaterialType,
        quantity: Double,
        unit: String,
        imageUri: String?
    ) {
        val now = System.currentTimeMillis()
        materialDao.insertMaterial(
            MaterialEntity(
                id = UUID.randomUUID().toString(),
                type = type.storageValue,
                name = name,
                imageUri = imageUri,
                color = null,
                fiber = null,
                weight = null,
                quantity = quantity,
                unit = unit,
                lengthMeters = null,
                costCents = null,
                purchasedAt = now,
                createdAt = now,
                updatedAt = now
            )
        )
    }
}
