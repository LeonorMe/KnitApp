package com.malha.app.data.material

import com.malha.app.core.firebase.AuthService
import com.malha.app.core.firebase.CloudDataService
import com.malha.app.domain.model.Material
import com.malha.app.domain.model.MaterialType
import kotlinx.coroutines.flow.Flow

class CloudBackedMaterialRepository(
    private val local: MaterialRepository,
    private val authService: AuthService,
    private val cloudDataService: CloudDataService
) : MaterialRepository {
    override fun observeMaterials(): Flow<List<Material>> = local.observeMaterials()

    override suspend fun createMaterial(
        name: String,
        type: MaterialType,
        quantity: Double,
        unit: String,
        imageUri: String?
    ): String {
        val materialId = local.createMaterial(name, type, quantity, unit, imageUri)
        runCatching {
            authService.currentUser?.let { user ->
                cloudDataService.upsertMaterial(
                    userId = user.id,
                    materialId = materialId,
                    name = name,
                    type = type,
                    quantity = quantity,
                    unit = unit,
                    imageUri = imageUri
                )
            }
        }
        return materialId
    }
}
