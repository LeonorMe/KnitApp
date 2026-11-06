package com.malha.app.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.malha.app.core.database.entity.MaterialEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MaterialDao {
    @Query("SELECT * FROM materials ORDER BY updatedAt DESC")
    fun observeMaterials(): Flow<List<MaterialEntity>>

    @Query("SELECT COUNT(*) FROM materials")
    suspend fun countMaterials(): Int

    @Query("SELECT COUNT(*) FROM materials WHERE id = :materialId")
    suspend fun countMaterialsById(materialId: String): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMaterials(materials: List<MaterialEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMaterial(material: MaterialEntity)

    @Query("SELECT * FROM materials WHERE id = :materialId LIMIT 1")
    suspend fun getMaterialById(materialId: String): MaterialEntity?

    @Query("DELETE FROM materials WHERE id = :materialId")
    suspend fun deleteMaterial(materialId: String): Int
}
