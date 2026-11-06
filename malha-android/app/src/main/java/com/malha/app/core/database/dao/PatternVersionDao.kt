package com.malha.app.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.malha.app.core.database.entity.InstructionNodeEntity
import com.malha.app.core.database.entity.PatternParameterEntity
import com.malha.app.core.database.entity.PatternSizeEntity
import com.malha.app.core.database.entity.PatternVersionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PatternVersionDao {

    @Query("SELECT * FROM pattern_versions WHERE id = :versionId LIMIT 1")
    suspend fun getVersionById(versionId: String): PatternVersionEntity?

    @Query("SELECT * FROM pattern_versions WHERE patternId = :patternId ORDER BY versionNumber DESC LIMIT 1")
    suspend fun getLatestVersionForPattern(patternId: String): PatternVersionEntity?

    @Query("SELECT * FROM pattern_versions WHERE patternId = :patternId ORDER BY versionNumber DESC")
    fun observeVersionsForPattern(patternId: String): Flow<List<PatternVersionEntity>>

    @Query("SELECT * FROM pattern_sizes WHERE patternVersionId = :versionId ORDER BY orderIndex ASC")
    suspend fun getSizesForVersion(versionId: String): List<PatternSizeEntity>

    @Query("SELECT * FROM pattern_parameters WHERE patternVersionId = :versionId")
    suspend fun getParametersForVersion(versionId: String): List<PatternParameterEntity>

    @Query("SELECT * FROM instruction_nodes WHERE sectionId = :sectionId ORDER BY orderIndex ASC")
    suspend fun getNodesForSection(sectionId: String): List<InstructionNodeEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVersion(version: PatternVersionEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSizes(sizes: List<PatternSizeEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertParameters(parameters: List<PatternParameterEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInstructionNodes(nodes: List<InstructionNodeEntity>)

    @Query("""
        SELECT COUNT(*) FROM projects 
        WHERE patternId = :patternId AND (status = 'ACTIVE' OR status = 'active' OR status = 'READY' OR status = 'ready')
    """)
    suspend fun countActiveProjectsUsingPattern(patternId: String): Int

    @Query("""
        SELECT COUNT(*) FROM projects 
        WHERE patternId = :patternId AND (status = 'DRAFT' OR status = 'draft')
    """)
    suspend fun countDraftProjectsUsingPattern(patternId: String): Int
}
