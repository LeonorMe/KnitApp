package com.malha.app.data.material

import com.malha.app.domain.model.Material

interface MaterialRepository {
    suspend fun getMaterials(): List<Material>
}

