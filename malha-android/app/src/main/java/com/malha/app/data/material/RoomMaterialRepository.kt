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
    ): String {
        val now = System.currentTimeMillis()
        val materialId = UUID.randomUUID().toString()
        materialDao.insertMaterial(
            MaterialEntity(
                id = materialId,
                name = name,
                imageUri = imageUri,
                type = type.storageValue,
                quantity = quantity,
                unit = unit,
                fiber = null,
                gramsPerBall = null,
                needleType = null,
                sizeMm = null,
                lengthCm = null,
                updatedAt = now
            )
        )
        return materialId
    }
}
