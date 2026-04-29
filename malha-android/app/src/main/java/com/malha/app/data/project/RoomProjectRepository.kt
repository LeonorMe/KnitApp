package com.malha.app.data.project

import com.malha.app.core.database.dao.ProjectDao
import com.malha.app.data.mapper.toDomain
import com.malha.app.domain.model.Project
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class RoomProjectRepository(
    private val projectDao: ProjectDao
) : ProjectRepository {
    override fun observeActiveProjects(): Flow<List<Project>> {
        return projectDao.observeActiveProjects()
            .map { projects -> projects.map { it.toDomain() } }
    }
}

