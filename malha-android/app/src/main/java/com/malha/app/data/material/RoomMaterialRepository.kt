package com.malha.app.data.material

import com.malha.app.core.database.dao.MaterialDao
import com.malha.app.data.mapper.toDomain
import com.malha.app.domain.model.Material
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class RoomMaterialRepository(
    private val materialDao: MaterialDao
) : MaterialRepository {
    override fun observeMaterials(): Flow<List<Material>> {
        return materialDao.observeMaterials()
            .map { materials -> materials.map { it.toDomain() } }
    }
}

