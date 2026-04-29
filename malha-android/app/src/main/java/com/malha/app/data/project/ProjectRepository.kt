package com.malha.app.data.project

import com.malha.app.domain.model.Project
import kotlinx.coroutines.flow.Flow

interface ProjectRepository {
    fun observeActiveProjects(): Flow<List<Project>>
    suspend fun createProject(name: String, patternId: String?)
    suspend fun archiveProject(projectId: String)
}
