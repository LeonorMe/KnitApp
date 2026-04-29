package com.malha.app.data.material

import com.malha.app.domain.model.Material
import kotlinx.coroutines.flow.Flow

interface MaterialRepository {
    fun observeMaterials(): Flow<List<Material>>
    suspend fun createYarn(name: String, quantity: Double, unit: String)
}
