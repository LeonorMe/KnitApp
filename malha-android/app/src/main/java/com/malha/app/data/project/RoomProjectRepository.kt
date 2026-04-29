package com.malha.app.data.project

import com.malha.app.core.database.dao.ProjectDao
import com.malha.app.core.database.entity.ProjectEntity
import com.malha.app.data.mapper.toDomain
import com.malha.app.domain.model.Project
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID

class RoomProjectRepository(
    private val projectDao: ProjectDao
) : ProjectRepository {
    override fun observeActiveProjects(): Flow<List<Project>> {
        return projectDao.observeActiveProjects()
            .map { projects -> projects.map { it.toDomain() } }
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
}
