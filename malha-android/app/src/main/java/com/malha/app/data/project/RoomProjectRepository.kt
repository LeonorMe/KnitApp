package com.malha.app.data.project

import com.malha.app.core.database.dao.ProjectDao
import com.malha.app.core.database.dao.ProjectStepProgressDao
import com.malha.app.core.database.entity.ProjectEntity
import com.malha.app.core.database.entity.ProjectStepProgressEntity
import com.malha.app.data.mapper.toDomain
import com.malha.app.domain.model.Project
import com.malha.app.domain.model.ProjectStepProgress
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID

class RoomProjectRepository(
    private val projectDao: ProjectDao,
    private val projectStepProgressDao: ProjectStepProgressDao
) : ProjectRepository {
    override fun observeActiveProjects(): Flow<List<Project>> {
        return projectDao.observeActiveProjects()
            .map { projects -> projects.map { it.toDomain() } }
    }

    override fun observeAllProjects(): Flow<List<Project>> {
        return projectDao.observeAllProjects()
            .map { projects -> projects.map { it.toDomain() } }
    }

    override fun observeProject(projectId: String): Flow<Project?> {
        return projectDao.observeProject(projectId)
            .map { project -> project?.toDomain() }
    }

    override suspend fun getProject(projectId: String): Project? {
        return projectDao.getProjectById(projectId)?.toDomain()
    }

    override fun observeStepProgress(
        projectId: String,
        patternStepId: String
    ): Flow<ProjectStepProgress?> {
        return projectStepProgressDao.observeStepProgress(projectId, patternStepId)
            .map { progress -> progress?.toDomain() }
    }

    override suspend fun createProject(name: String, patternId: String?): String {
        val now = System.currentTimeMillis()
        val projectId = UUID.randomUUID().toString()
        val initialStatus = if (patternId != null) com.malha.app.domain.model.ProjectStatus.READY.value else com.malha.app.domain.model.ProjectStatus.DRAFT.value
        projectDao.insertProject(
            ProjectEntity(
                id = projectId,
                name = name,
                imageUri = null,
                craftType = "knitting",
                patternId = patternId,
                status = initialStatus,
                currentStepIndex = 0,
                progressPercent = 0,
                startedAt = null,
                completedAt = null,
                createdAt = now,
                updatedAt = now
            )
        )
        return projectId
    }

    override suspend fun duplicateProject(sourceProjectId: String, newName: String?): String {
        val source = projectDao.getProjectById(sourceProjectId)
            ?: throw IllegalArgumentException("Source project not found")
        val now = System.currentTimeMillis()
        val newProjectId = UUID.randomUUID().toString()
        val finalName = newName?.trim()?.takeIf { it.isNotEmpty() } ?: "${source.name} (Copy)"

        val newProject = ProjectEntity(
            id = newProjectId,
            name = finalName,
            imageUri = source.imageUri,
            craftType = source.craftType,
            patternId = source.patternId,
            patternTitle = source.patternTitle,
            patternSnapshotJson = source.patternSnapshotJson,
            status = if (source.patternId != null && source.selectedSize != null) {
                com.malha.app.domain.model.ProjectStatus.READY.value
            } else {
                com.malha.app.domain.model.ProjectStatus.DRAFT.value
            },
            currentStepIndex = 0,
            progressPercent = 0,
            startedAt = null,
            completedAt = null,
            createdAt = now,
            updatedAt = now,
            colorHex = source.colorHex,
            selectedSize = source.selectedSize
        )
        projectDao.insertProject(newProject)

        // Duplicate materials
        val sourceMaterials = projectDao.getProjectMaterials(sourceProjectId)
        if (sourceMaterials.isNotEmpty()) {
            projectDao.insertProjectMaterials(
                sourceMaterials.map {
                    it.copy(
                        id = "$newProjectId:${it.materialId}",
                        projectId = newProjectId
                    )
                }
            )
        }
        return newProjectId
    }

    override suspend fun startProject(projectId: String) {
        val now = System.currentTimeMillis()
        projectDao.updateProjectStatus(
            projectId = projectId,
            status = com.malha.app.domain.model.ProjectStatus.ACTIVE.value,
            startedAt = now,
            updatedAt = now
        )
    }

    override suspend fun archiveProject(projectId: String) {
        projectDao.archiveProject(
            projectId = projectId,
            updatedAt = System.currentTimeMillis()
        )
    }

    override suspend fun deleteProject(projectId: String) {
        projectDao.deleteProjectMaterials(projectId)
        projectDao.deleteProject(projectId)
    }

    override suspend fun completeProject(projectId: String, photoUri: String?) {
        projectDao.completeProject(
            projectId = projectId,
            completedAt = System.currentTimeMillis(),
            imageUri = photoUri
        )
    }

    override suspend fun updateProgress(
        projectId: String,
        stepIndex: Int,
        progressPercent: Int
    ) {
        projectDao.updateProgress(
            projectId = projectId,
            stepIndex = stepIndex,
            progressPercent = progressPercent,
            updatedAt = System.currentTimeMillis()
        )
    }

    override suspend fun updateProjectColor(projectId: String, colorHex: String?) {
        projectDao.updateColor(
            projectId = projectId,
            colorHex = colorHex,
            updatedAt = System.currentTimeMillis()
        )
    }

    override suspend fun saveStepProgress(
        projectId: String,
        patternStepId: String,
        isDone: Boolean,
        note: String?
    ) {
        projectStepProgressDao.upsertStepProgress(
            ProjectStepProgressEntity(
                id = "$projectId:$patternStepId",
                projectId = projectId,
                patternStepId = patternStepId,
                isDone = isDone,
                doneAt = if (isDone) System.currentTimeMillis() else null,
                note = note?.takeIf { it.isNotBlank() }
            )
        )
    }

    override suspend fun updateProjectSettings(
        projectId: String,
        name: String,
        colorHex: String?,
        selectedSize: String?,
        materialIds: List<String>
    ) {
        val now = System.currentTimeMillis()
        projectDao.updateProjectSettings(
            projectId = projectId,
            name = name,
            colorHex = colorHex,
            selectedSize = selectedSize,
            updatedAt = now
        )
        projectDao.deleteProjectMaterials(projectId)
        projectDao.insertProjectMaterials(
            materialIds.map { materialId ->
                com.malha.app.core.database.entity.ProjectMaterialEntity(
                    id = "$projectId:$materialId",
                    projectId = projectId,
                    materialId = materialId,
                    plannedQuantity = null,
                    usedQuantity = null
                )
            }
        )
    }

    override suspend fun updateProjectConfiguration(
        projectId: String,
        name: String,
        colorHex: String?,
        selectedSize: String?,
        patternId: String?,
        patternTitle: String?,
        patternSnapshotJson: String?,
        materialIds: List<String>,
        status: String
    ) {
        val now = System.currentTimeMillis()
        projectDao.updateProjectSettingsWithPattern(
            projectId = projectId,
            name = name,
            colorHex = colorHex,
            selectedSize = selectedSize,
            patternId = patternId,
            patternTitle = patternTitle,
            patternSnapshotJson = patternSnapshotJson,
            status = status,
            updatedAt = now
        )
        projectDao.deleteProjectMaterials(projectId)
        projectDao.insertProjectMaterials(
            materialIds.map { materialId ->
                com.malha.app.core.database.entity.ProjectMaterialEntity(
                    id = "$projectId:$materialId",
                    projectId = projectId,
                    materialId = materialId,
                    plannedQuantity = null,
                    usedQuantity = null
                )
            }
        )
    }

    override fun observeProjectMaterials(projectId: String): Flow<List<String>> {
        return projectDao.observeProjectMaterials(projectId)
            .map { list -> list.map { it.materialId } }
    }
}

