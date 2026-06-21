package com.malha.app.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.malha.app.core.database.entity.VariantEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface VariantDao {
    @Query("SELECT * FROM variants WHERE patternId = :patternId ORDER BY updatedAt DESC")
    fun observeVariants(patternId: String): Flow<List<VariantEntity>>

    @Query("SELECT * FROM variants WHERE id = :variantId LIMIT 1")
    suspend fun getVariant(variantId: String): VariantEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVariants(variants: List<VariantEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVariant(variant: VariantEntity)
}
