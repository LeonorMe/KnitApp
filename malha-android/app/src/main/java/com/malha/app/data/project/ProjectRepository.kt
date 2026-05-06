package com.malha.app.data.project

import com.malha.app.domain.model.Project
import kotlinx.coroutines.flow.Flow

interface ProjectRepository {
    fun observeActiveProjects(): Flow<List<Project>>
    fun observeProject(projectId: String): Flow<Project?>
    suspend fun createProject(name: String, patternId: String?)
    suspend fun archiveProject(projectId: String)
    suspend fun updateProgress(projectId: String, stepIndex: Int, progressPercent: Int)
}
