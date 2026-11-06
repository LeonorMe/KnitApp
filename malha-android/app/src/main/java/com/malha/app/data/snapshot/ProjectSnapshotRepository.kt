package com.malha.app.data.snapshot

import androidx.room.withTransaction
import com.malha.app.core.database.MalhaDatabase
import com.malha.app.core.database.dao.InstructionProgressDao
import com.malha.app.core.database.dao.PatternDao
import com.malha.app.core.database.dao.PatternVersionDao
import com.malha.app.core.database.dao.ProjectSnapshotDao
import com.malha.app.core.database.entity.InstructionNodeEntity
import com.malha.app.core.database.entity.InstructionProgressEntity
import com.malha.app.core.database.entity.ProjectInstructionSnapshotEntity
import com.malha.app.core.database.entity.ProjectPatternSnapshotEntity
import com.malha.app.domain.engine.PatternExpansionEngine
import com.malha.app.domain.pattern.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID

interface ProjectSnapshotRepository {
    fun observeSnapshot(projectId: String): Flow<ProjectPatternSnapshot?>
    suspend fun getSnapshot(projectId: String): ProjectPatternSnapshot?
    fun observeInstructions(snapshotId: String): Flow<List<ProjectInstructionSnapshot>>
    suspend fun getInstructions(snapshotId: String): List<ProjectInstructionSnapshot>
    fun observeProgress(projectId: String): Flow<List<InstructionProgress>>
    suspend fun updateCount(projectId: String, instructionId: String, count: Int, increment: Int)
    suspend fun markCompleted(projectId: String, instructionId: String, completed: Boolean)
    suspend fun saveNote(projectId: String, instructionId: String, note: String?)
    suspend fun createAndSaveSnapshot(projectId: String, patternId: String, sizeKey: String): ProjectPatternSnapshot
    suspend fun countActiveProjectsUsingPattern(patternId: String): Int
}

class RoomProjectSnapshotRepository(
    private val database: MalhaDatabase,
    private val projectSnapshotDao: ProjectSnapshotDao,
    private val instructionProgressDao: InstructionProgressDao,
    private val patternVersionDao: PatternVersionDao,
    private val patternDao: PatternDao
) : ProjectSnapshotRepository {

    override fun observeSnapshot(projectId: String): Flow<ProjectPatternSnapshot?> {
        return projectSnapshotDao.observeSnapshotForProject(projectId).map { entity ->
            entity?.toDomain()
        }
    }

    override suspend fun getSnapshot(projectId: String): ProjectPatternSnapshot? {
        val entity = projectSnapshotDao.getSnapshotForProject(projectId) ?: return null
        val instructions = projectSnapshotDao.getInstructionsForSnapshot(entity.id).map { it.toDomain() }
        return entity.toDomain(instructions)
    }

    override fun observeInstructions(snapshotId: String): Flow<List<ProjectInstructionSnapshot>> {
        return projectSnapshotDao.observeInstructionsForSnapshot(snapshotId).map { list ->
            list.map { it.toDomain() }
        }
    }

    override suspend fun getInstructions(snapshotId: String): List<ProjectInstructionSnapshot> {
        return projectSnapshotDao.getInstructionsForSnapshot(snapshotId).map { it.toDomain() }
    }

    override fun observeProgress(projectId: String): Flow<List<InstructionProgress>> {
        return instructionProgressDao.observeProgressForProject(projectId).map { list ->
            list.map { it.toDomain() }
        }
    }

    override suspend fun updateCount(projectId: String, instructionId: String, count: Int, increment: Int) {
        val existing = instructionProgressDao.getProgressForInstruction(projectId, instructionId)
        if (existing == null) {
            instructionProgressDao.insertProgress(
                InstructionProgressEntity(
                    id = UUID.randomUUID().toString(),
                    projectId = projectId,
                    instructionSnapshotId = instructionId,
                    currentCount = count,
                    isCompleted = false,
                    selectedIncrement = increment
                )
            )
        } else {
            instructionProgressDao.updateCount(projectId, instructionId, count, increment)
        }
    }

    override suspend fun markCompleted(projectId: String, instructionId: String, completed: Boolean) {
        val completedAt = if (completed) System.currentTimeMillis() else null
        val existing = instructionProgressDao.getProgressForInstruction(projectId, instructionId)
        if (existing == null) {
            instructionProgressDao.insertProgress(
                InstructionProgressEntity(
                    id = UUID.randomUUID().toString(),
                    projectId = projectId,
                    instructionSnapshotId = instructionId,
                    currentCount = 0,
                    isCompleted = completed,
                    completedAt = completedAt,
                    selectedIncrement = 1
                )
            )
        } else {
            instructionProgressDao.markCompleted(projectId, instructionId, completed, completedAt)
        }
    }

    override suspend fun saveNote(projectId: String, instructionId: String, note: String?) {
        val existing = instructionProgressDao.getProgressForInstruction(projectId, instructionId)
        if (existing == null) {
            instructionProgressDao.insertProgress(
                InstructionProgressEntity(
                    id = UUID.randomUUID().toString(),
                    projectId = projectId,
                    instructionSnapshotId = instructionId,
                    note = note
                )
            )
        } else {
            instructionProgressDao.updateNote(projectId, instructionId, note)
        }
    }

    override suspend fun createAndSaveSnapshot(
        projectId: String,
        patternId: String,
        sizeKey: String
    ): ProjectPatternSnapshot {
        val versionDomain = loadOrCreatePatternVersion(patternId, sizeKey)
        val snapshot = PatternExpansionEngine.expand(projectId, versionDomain, sizeKey)

        database.withTransaction {
            projectSnapshotDao.deleteSnapshotForProject(projectId)
            projectSnapshotDao.insertSnapshot(
                ProjectPatternSnapshotEntity(
                    id = snapshot.id,
                    projectId = snapshot.projectId,
                    sourcePatternId = snapshot.sourcePatternId,
                    sourceVersionId = snapshot.sourceVersionId,
                    selectedSizeKey = snapshot.selectedSizeKey,
                    schemaVersion = snapshot.schemaVersion,
                    checksum = snapshot.checksum,
                    totalInstructionCount = snapshot.totalInstructionCount,
                    createdAt = snapshot.createdAt
                )
            )

            val instructionEntities = snapshot.instructions.map { inst ->
                ProjectInstructionSnapshotEntity(
                    id = inst.id,
                    snapshotId = inst.snapshotId,
                    ordinal = inst.ordinal,
                    stablePath = inst.stablePath,
                    type = inst.type.name,
                    techniqueId = inst.techniqueId,
                    targetCount = inst.targetCount,
                    countUnit = inst.countUnit.name,
                    sectionTitle = inst.sectionTitle,
                    displayText = inst.displayText,
                    measurementValue = inst.measurementValue,
                    measurementUnit = inst.measurementUnit,
                    isDestructive = inst.isDestructive
                )
            }
            projectSnapshotDao.insertInstructions(instructionEntities)

            // Initialize progress entities
            val progressEntities = snapshot.instructions.map { inst ->
                InstructionProgressEntity(
                    id = UUID.randomUUID().toString(),
                    projectId = projectId,
                    instructionSnapshotId = inst.id,
                    currentCount = 0,
                    isCompleted = false,
                    selectedIncrement = 1
                )
            }
            instructionProgressDao.deleteProgressForProject(projectId)
            instructionProgressDao.insertAllProgress(progressEntities)
        }

        return snapshot
    }

    override suspend fun countActiveProjectsUsingPattern(patternId: String): Int {
        return patternVersionDao.countActiveProjectsUsingPattern(patternId)
    }

    private suspend fun loadOrCreatePatternVersion(patternId: String, sizeKey: String): PatternVersion {
        val versionEntity = patternVersionDao.getLatestVersionForPattern(patternId)
        if (versionEntity != null) {
            val sizes = patternVersionDao.getSizesForVersion(versionEntity.id).map {
                PatternSize(
                    id = it.id,
                    patternVersionId = it.patternVersionId,
                    key = it.sizeKey,
                    displayName = it.displayName,
                    order = it.orderIndex,
                    measurementsJson = it.measurementsJson
                )
            }
            val params = patternVersionDao.getParametersForVersion(versionEntity.id).map {
                PatternParameter(
                    id = it.id,
                    patternVersionId = it.patternVersionId,
                    sizeKey = it.sizeKey,
                    paramKey = it.paramKey,
                    value = it.value,
                    unit = it.unit
                )
            }

            val patternWithSteps = patternDao.getPatternWithStepsById(patternId)
            val legacySections = patternWithSteps?.sections ?: emptyList()
            val sections = legacySections.map { sec ->
                val allNodes = patternVersionDao.getNodesForSection(sec.id)
                val topLevelNodes = allNodes.filter { it.parentNodeId == null }
                val domainNodes = topLevelNodes.map { node ->
                    buildInstructionNode(node, allNodes)
                }

                CompressedSection(
                    id = sec.id,
                    patternVersionId = versionEntity.id,
                    order = sec.orderIndex,
                    title = sec.name,
                    nodes = domainNodes
                )
            }

            return PatternVersion(
                id = versionEntity.id,
                patternId = versionEntity.patternId,
                versionNumber = versionEntity.versionNumber,
                schemaVersion = versionEntity.schemaVersion,
                status = versionEntity.status,
                createdAt = versionEntity.createdAt,
                checksum = versionEntity.checksum,
                sizes = sizes,
                parameters = params,
                sections = sections
            )
        }

        // Fallback for legacy patterns that don't have a pattern_version row yet:
        val patternWithSteps = patternDao.getPatternWithStepsById(patternId)
            ?: throw IllegalArgumentException("Pattern not found: $patternId")
        val legacySections = patternWithSteps.sections
        val legacySteps = patternWithSteps.steps

        val generatedVersionId = "v1_$patternId"
        val sizeList = listOf(
            PatternSize(
                id = "size_$sizeKey",
                patternVersionId = generatedVersionId,
                key = sizeKey,
                displayName = sizeKey,
                order = 0
            )
        )

        val sections = legacySections.map { sec ->
            val sectionSteps = legacySteps.filter { it.sectionId == sec.id }
            val nodes = sectionSteps.map { step ->
                val nodeType = when (step.stepType) {
                    com.malha.app.domain.model.StepType.REPEAT,
                    com.malha.app.domain.model.StepType.REPEAT_BLOCK ->
                        InstructionNodeType.REPEAT_BLOCK
                    com.malha.app.domain.model.StepType.FINISHING,
                    com.malha.app.domain.model.StepType.JOIN ->
                        InstructionNodeType.BINARY_ACTION
                    else -> InstructionNodeType.COUNTED_ACTION
                }
                InstructionNode(
                    id = step.id,
                    sectionId = sec.id,
                    order = step.orderIndex,
                    nodeType = nodeType,
                    techniqueId = step.stitchPatternId ?: step.stepType.name.lowercase(),
                    targetLiteral = step.stitchCount,
                    countUnit = CountUnit.STITCH,
                    repeatTimesLiteral = step.repeatCount,
                    displayText = step.instruction,
                    isDestructive = (step.stepType == com.malha.app.domain.model.StepType.BIND_OFF)
                )
            }
            CompressedSection(
                id = sec.id,
                patternVersionId = generatedVersionId,
                order = sec.orderIndex,
                title = sec.name,
                nodes = nodes
            )
        }

        return PatternVersion(
            id = generatedVersionId,
            patternId = patternId,
            versionNumber = 1,
            schemaVersion = 1,
            status = "PUBLISHED",
            sizes = sizeList,
            parameters = emptyList(),
            sections = sections
        )
    }

    private fun buildInstructionNode(
        node: InstructionNodeEntity,
        allNodes: List<InstructionNodeEntity>
    ): InstructionNode {
        val childEntities = allNodes.filter { it.parentNodeId == node.id }
        val childNodes = childEntities.map { buildInstructionNode(it, allNodes) }
        val nType = runCatching { InstructionNodeType.valueOf(node.nodeType) }
            .getOrDefault(InstructionNodeType.COUNTED_ACTION)
        val cUnit = runCatching { CountUnit.valueOf(node.countUnit) }
            .getOrDefault(CountUnit.STITCH)

        return InstructionNode(
            id = node.id,
            sectionId = node.sectionId,
            parentNodeId = node.parentNodeId,
            order = node.orderIndex,
            nodeType = nType,
            techniqueId = node.techniqueId,
            targetLiteral = node.targetLiteral,
            targetParamKey = node.targetParamKey,
            countUnit = cUnit,
            repeatTimesLiteral = node.repeatTimesLiteral,
            repeatTimesParamKey = node.repeatTimesParamKey,
            displayText = node.displayText,
            measurementValue = node.measurementValue,
            measurementUnit = node.measurementUnit,
            isDestructive = node.isDestructive,
            childNodes = childNodes
        )
    }

    private fun ProjectPatternSnapshotEntity.toDomain(
        instructions: List<ProjectInstructionSnapshot> = emptyList()
    ): ProjectPatternSnapshot {
        return ProjectPatternSnapshot(
            id = id,
            projectId = projectId,
            sourcePatternId = sourcePatternId,
            sourceVersionId = sourceVersionId,
            selectedSizeKey = selectedSizeKey,
            schemaVersion = schemaVersion,
            checksum = checksum,
            totalInstructionCount = totalInstructionCount,
            createdAt = createdAt,
            instructions = instructions
        )
    }

    private fun ProjectInstructionSnapshotEntity.toDomain(): ProjectInstructionSnapshot {
        val nType = runCatching { InstructionNodeType.valueOf(type) }
            .getOrDefault(InstructionNodeType.COUNTED_ACTION)
        val cUnit = runCatching { CountUnit.valueOf(countUnit) }
            .getOrDefault(CountUnit.STITCH)

        return ProjectInstructionSnapshot(
            id = id,
            snapshotId = snapshotId,
            ordinal = ordinal,
            stablePath = stablePath,
            type = nType,
            techniqueId = techniqueId,
            targetCount = targetCount,
            countUnit = cUnit,
            sectionTitle = sectionTitle,
            displayText = displayText,
            measurementValue = measurementValue,
            measurementUnit = measurementUnit,
            isDestructive = isDestructive
        )
    }

    private fun InstructionProgressEntity.toDomain(): InstructionProgress {
        return InstructionProgress(
            projectId = projectId,
            instructionSnapshotId = instructionSnapshotId,
            currentCount = currentCount,
            isCompleted = isCompleted,
            completedAt = completedAt,
            selectedIncrement = selectedIncrement,
            note = note
        )
    }
}
