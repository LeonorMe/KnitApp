package com.malha.app.data.technique

import com.malha.app.core.database.dao.TechniqueDao
import com.malha.app.core.database.entity.TechniqueAliasEntity
import com.malha.app.core.database.entity.TechniqueEntity
import com.malha.app.domain.model.CraftType
import com.malha.app.domain.pattern.Technique
import com.malha.app.domain.pattern.TechniqueAlias
import com.malha.app.domain.pattern.TechniqueSourceType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID

interface TechniqueRepository {
    fun observeAllTechniques(): Flow<List<Technique>>
    fun observeTechniquesByCraft(craftType: CraftType): Flow<List<Technique>>
    suspend fun getTechniqueById(id: String): Technique?
    suspend fun searchTechniques(query: String): List<Technique>
    suspend fun insertCustomTechnique(technique: Technique, aliases: List<String>)
    suspend fun updateCustomTechnique(technique: Technique, aliases: List<String>)
    suspend fun getAliasesForTechnique(techniqueId: String): List<TechniqueAlias>
}

class RoomTechniqueRepository(
    private val techniqueDao: TechniqueDao
) : TechniqueRepository {

    override fun observeAllTechniques(): Flow<List<Technique>> {
        return techniqueDao.observeAllTechniques().map { list ->
            list.map { it.toDomain() }
        }
    }

    override fun observeTechniquesByCraft(craftType: CraftType): Flow<List<Technique>> {
        return techniqueDao.observeTechniquesByCraft(craftType.name).map { list ->
            list.map { it.toDomain() }
        }
    }

    override suspend fun getTechniqueById(id: String): Technique? {
        return techniqueDao.getTechniqueById(id)?.toDomain()
    }

    override suspend fun searchTechniques(query: String): List<Technique> {
        val normalized = TechniqueAlias.normalize(query)
        return techniqueDao.searchTechniques(query, normalized).map { it.toDomain() }
    }

    override suspend fun insertCustomTechnique(technique: Technique, aliases: List<String>) {
        techniqueDao.insertTechnique(technique.toEntity())
        val aliasEntities = aliases.map { alias ->
            TechniqueAliasEntity(
                id = UUID.randomUUID().toString(),
                techniqueId = technique.id,
                normalizedAlias = TechniqueAlias.normalize(alias),
                locale = "en",
                displayAlias = alias.trim()
            )
        }
        techniqueDao.insertAliases(aliasEntities)
    }

    override suspend fun updateCustomTechnique(technique: Technique, aliases: List<String>) {
        insertCustomTechnique(technique, aliases)
    }

    override suspend fun getAliasesForTechnique(techniqueId: String): List<TechniqueAlias> {
        return techniqueDao.getAliasesForTechnique(techniqueId).map { entity ->
            TechniqueAlias(
                id = entity.id,
                techniqueId = entity.techniqueId,
                normalizedAlias = entity.normalizedAlias,
                locale = entity.locale,
                displayAlias = entity.displayAlias
            )
        }
    }

    private fun TechniqueEntity.toDomain(): Technique {
        val craft = runCatching { CraftType.valueOf(craftType) }.getOrDefault(CraftType.KNITTING)
        val source = runCatching { TechniqueSourceType.valueOf(sourceType) }.getOrDefault(TechniqueSourceType.OFFICIAL)
        return Technique(
            id = id,
            craftType = craft,
            canonicalNameKey = canonicalNameKey,
            descriptionKey = descriptionKey,
            customName = customName,
            customDescription = customDescription,
            mediaUrl = mediaUrl,
            sourceType = source,
            ownerId = ownerId,
            version = version,
            isArchived = isArchived
        )
    }

    private fun Technique.toEntity(): TechniqueEntity {
        return TechniqueEntity(
            id = id,
            craftType = craftType.name,
            canonicalNameKey = canonicalNameKey,
            descriptionKey = descriptionKey,
            customName = customName,
            customDescription = customDescription,
            mediaUrl = mediaUrl,
            sourceType = sourceType.name,
            ownerId = ownerId,
            version = version,
            isArchived = isArchived
        )
    }
}
