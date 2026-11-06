package com.malha.app.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.malha.app.core.database.entity.TechniqueAliasEntity
import com.malha.app.core.database.entity.TechniqueEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TechniqueDao {

    @Query("SELECT * FROM techniques WHERE isArchived = 0 ORDER BY canonicalNameKey ASC")
    fun observeAllTechniques(): Flow<List<TechniqueEntity>>

    @Query("SELECT * FROM techniques WHERE craftType = :craftType AND isArchived = 0 ORDER BY canonicalNameKey ASC")
    fun observeTechniquesByCraft(craftType: String): Flow<List<TechniqueEntity>>

    @Query("SELECT * FROM techniques WHERE id = :id LIMIT 1")
    suspend fun getTechniqueById(id: String): TechniqueEntity?

    @Query("""
        SELECT DISTINCT t.* FROM techniques t
        LEFT JOIN technique_aliases a ON t.id = a.techniqueId
        WHERE t.isArchived = 0 AND (
            t.canonicalNameKey LIKE '%' || :query || '%'
            OR t.customName LIKE '%' || :query || '%'
            OR a.normalizedAlias LIKE '%' || :normalizedQuery || '%'
            OR a.displayAlias LIKE '%' || :query || '%'
        )
        ORDER BY t.canonicalNameKey ASC
    """)
    suspend fun searchTechniques(query: String, normalizedQuery: String): List<TechniqueEntity>

    @Query("SELECT * FROM technique_aliases WHERE techniqueId = :techniqueId")
    suspend fun getAliasesForTechnique(techniqueId: String): List<TechniqueAliasEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTechnique(technique: TechniqueEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTechniques(techniques: List<TechniqueEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAlias(alias: TechniqueAliasEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAliases(aliases: List<TechniqueAliasEntity>)

    @Query("SELECT COUNT(*) FROM techniques")
    suspend fun countTechniques(): Int
}
