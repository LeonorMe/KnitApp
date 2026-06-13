package com.malha.app.data.project

import com.malha.app.domain.model.Project
import com.malha.app.domain.model.ProjectStepProgress
import kotlinx.coroutines.flow.Flow

interface ProjectRepository {
    fun observeActiveProjects(): Flow<List<Project>>
    fun observeProject(projectId: String): Flow<Project?>
    fun observeStepProgress(projectId: String, patternStepId: String): Flow<ProjectStepProgress?>
    suspend fun createProject(name: String, patternId: String?): String
    suspend fun archiveProject(projectId: String)
    suspend fun updateProgress(projectId: String, stepIndex: Int, progressPercent: Int)
    suspend fun updateProjectColor(projectId: String, colorHex: String?)
    suspend fun saveStepProgress(
        projectId: String,
        patternStepId: String,
        isDone: Boolean,
        note: String?
    )
    suspend fun updateProjectSettings(
        projectId: String,
        name: String,
        colorHex: String?,
        selectedSize: String?,
        materialIds: List<String>
    )
    fun observeProjectMaterials(projectId: String): Flow<List<String>>
}
