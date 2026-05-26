package com.malha.app.data.material

import com.malha.app.domain.model.Material
import com.malha.app.domain.model.MaterialType
import kotlinx.coroutines.flow.Flow

interface MaterialRepository {
    fun observeMaterials(): Flow<List<Material>>
    suspend fun createMaterial(
        name: String,
        type: MaterialType,
        quantity: Double,
        unit: String,
        imageUri: String? = null
    ): String
}
