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

    override fun observeProject(projectId: String): Flow<Project?> {
        return projectDao.observeProject(projectId)
            .map { project -> project?.toDomain() }
    }

    override fun observeStepProgress(
        projectId: String,
        patternStepId: String
    ): Flow<ProjectStepProgress?> {
        return projectStepProgressDao.observeStepProgress(projectId, patternStepId)
            .map { progress -> progress?.toDomain() }
    }

    override suspend fun createProject(name: String, patternId: String?) {
        val now = System.currentTimeMillis()
        projectDao.insertProject(
            ProjectEntity(
                id = UUID.randomUUID().toString(),
                name = name,
                craftType = "knitting",
                patternId = patternId,
                status = "active",
                currentStepIndex = 0,
                progressPercent = 0,
                startedAt = now,
                completedAt = null,
                createdAt = now,
                updatedAt = now
            )
        )
    }

    override suspend fun archiveProject(projectId: String) {
        projectDao.archiveProject(
            projectId = projectId,
            updatedAt = System.currentTimeMillis()
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
}
