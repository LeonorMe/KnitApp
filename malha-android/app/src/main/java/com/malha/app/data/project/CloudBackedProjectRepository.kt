package com.malha.app.data.project

import com.malha.app.core.firebase.AuthService
import com.malha.app.core.firebase.CloudDataService
import com.malha.app.domain.model.Project
import com.malha.app.domain.model.ProjectStepProgress
import kotlinx.coroutines.flow.Flow

class CloudBackedProjectRepository(
    private val local: ProjectRepository,
    private val authService: AuthService,
    private val cloudDataService: CloudDataService
) : ProjectRepository {
    override fun observeActiveProjects(): Flow<List<Project>> = local.observeActiveProjects()

    override fun observeProject(projectId: String): Flow<Project?> = local.observeProject(projectId)

    override fun observeStepProgress(projectId: String, patternStepId: String): Flow<ProjectStepProgress?> {
        return local.observeStepProgress(projectId, patternStepId)
    }

    override suspend fun createProject(name: String, patternId: String?): String {
        val projectId = local.createProject(name, patternId)
        runCatching {
            authService.currentUser?.let { user ->
                cloudDataService.upsertProject(
                    userId = user.id,
                    projectId = projectId,
                    name = name,
                    patternId = patternId,
                    currentStepIndex = 0,
                    progressPercent = 0,
                    status = "active"
                )
            }
        }
        return projectId
    }

    override suspend fun archiveProject(projectId: String) {
        local.archiveProject(projectId)
        runCatching {
            authService.currentUser?.let { user ->
                cloudDataService.upsertProject(
                    userId = user.id,
                    projectId = projectId,
                    name = "",
                    patternId = null,
                    currentStepIndex = 0,
                    progressPercent = 0,
                    status = "archived"
                )
            }
        }
    }

    override suspend fun updateProgress(projectId: String, stepIndex: Int, progressPercent: Int) {
        local.updateProgress(projectId, stepIndex, progressPercent)
        runCatching {
            authService.currentUser?.let { user ->
                cloudDataService.upsertProject(
                    userId = user.id,
                    projectId = projectId,
                    name = "",
                    patternId = null,
                    currentStepIndex = stepIndex,
                    progressPercent = progressPercent,
                    status = "active"
                )
            }
        }
    }

    override suspend fun saveStepProgress(
        projectId: String,
        patternStepId: String,
        isDone: Boolean,
        note: String?
    ) {
        local.saveStepProgress(projectId, patternStepId, isDone, note)
    }
}
