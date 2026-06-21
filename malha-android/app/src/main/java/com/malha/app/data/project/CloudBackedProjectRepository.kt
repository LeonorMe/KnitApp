package com.malha.app.data.project

import android.util.Log
import com.malha.app.core.database.dao.ProjectDao
import com.malha.app.core.database.entity.ProjectEntity
import com.malha.app.core.database.entity.ProjectMaterialEntity
import com.malha.app.core.firebase.AuthService
import com.malha.app.core.firebase.CloudDataService
import com.malha.app.domain.model.Project
import com.malha.app.domain.model.ProjectStepProgress
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch

private const val TAG = "CloudProjectRepo"

@OptIn(ExperimentalCoroutinesApi::class)
class CloudBackedProjectRepository(
    private val local: ProjectRepository,
    private val projectDao: ProjectDao,
    private val authService: AuthService,
    private val cloudDataService: CloudDataService,
    private val syncScope: CoroutineScope
) : ProjectRepository {

    private var syncJob: Job? = null

    init {
        startProjectSync()
    }

    private fun startProjectSync() {
        syncJob?.cancel()
        syncJob = syncScope.launch {
            authService.authState
                .flatMapLatest { authUser ->
                    val userId = authUser?.id
                    if (userId != null) {
                        launch {
                            cloudDataService.observeProjects(userId)
                                .collectLatest { remoteProjects ->
                                    remoteProjects.forEach { map ->
                                        val id = map["id"] as? String ?: return@forEach
                                        val name = map["name"] as? String ?: ""
                                        val patternId = map["patternId"] as? String
                                        val currentStepIndex = (map["currentStepIndex"] as? Long)?.toInt() ?: 0
                                        val progressPercent = (map["progressPercent"] as? Long)?.toInt() ?: 0
                                        val status = map["status"] as? String ?: "active"
                                        val colorHex = map["colorHex"] as? String
                                        val selectedSize = map["selectedSize"] as? String
                                        val updatedAt = (map["updatedAt"] as? Long) ?: System.currentTimeMillis()

                                        projectDao.insertProject(
                                            ProjectEntity(
                                                id = id,
                                                name = name,
                                                imageUri = null,
                                                craftType = "knitting",
                                                patternId = patternId,
                                                status = status,
                                                currentStepIndex = currentStepIndex,
                                                progressPercent = progressPercent,
                                                startedAt = updatedAt,
                                                completedAt = null,
                                                createdAt = updatedAt,
                                                updatedAt = updatedAt,
                                                colorHex = colorHex,
                                                selectedSize = selectedSize
                                            )
                                        )
                                    }
                                }
                        }
                        launch {
                            cloudDataService.observeProjectMaterials(userId)
                                .collectLatest { remotePMs ->
                                    remotePMs.forEach { map ->
                                        val id = map["id"] as? String ?: return@forEach
                                        val projectId = map["projectId"] as? String ?: return@forEach
                                        val materialId = map["materialId"] as? String ?: return@forEach
                                        val plannedQuantity = map["plannedQuantity"] as? Double
                                        val usedQuantity = map["usedQuantity"] as? Double

                                        projectDao.insertProjectMaterials(
                                            listOf(
                                                ProjectMaterialEntity(
                                                    id = id,
                                                    projectId = projectId,
                                                    materialId = materialId,
                                                    plannedQuantity = plannedQuantity,
                                                    usedQuantity = usedQuantity
                                                )
                                            )
                                        )
                                    }
                                }
                        }
                        flowOf(null)
                    } else {
                        flowOf(null)
                    }
                }
                .collectLatest {}
        }
    }

    override fun observeActiveProjects(): Flow<List<Project>> = local.observeActiveProjects()

    override fun observeProject(projectId: String): Flow<Project?> = local.observeProject(projectId)

    override fun observeStepProgress(projectId: String, patternStepId: String): Flow<ProjectStepProgress?> {
        return local.observeStepProgress(projectId, patternStepId)
    }

    override suspend fun createProject(name: String, patternId: String?): String {
        val projectId = local.createProject(name, patternId)
        val user = authService.currentUser
        Log.d(TAG, "createProject: id=$projectId user=${user?.id}")
        if (user != null) {
            try {
                cloudDataService.upsertProject(
                    userId = user.id,
                    projectId = projectId,
                    name = name,
                    patternId = patternId,
                    currentStepIndex = 0,
                    progressPercent = 0,
                    status = "active"
                )
                Log.d(TAG, "createProject: Firestore write OK for $projectId")
            } catch (e: Exception) {
                Log.e(TAG, "createProject: Firestore write FAILED for $projectId", e)
            }
        } else {
            Log.w(TAG, "createProject: no authenticated user, skipping Firestore write")
        }
        return projectId
    }

    override suspend fun archiveProject(projectId: String) {
        local.archiveProject(projectId)
        val user = authService.currentUser
        if (user != null) {
            try {
                cloudDataService.upsertProject(
                    userId = user.id,
                    projectId = projectId,
                    name = "",
                    patternId = null,
                    currentStepIndex = 0,
                    progressPercent = 0,
                    status = "archived"
                )
            } catch (e: Exception) {
                Log.e(TAG, "archiveProject: Firestore write FAILED for $projectId", e)
            }
        }
    }

    override suspend fun updateProgress(projectId: String, stepIndex: Int, progressPercent: Int) {
        local.updateProgress(projectId, stepIndex, progressPercent)
        val user = authService.currentUser
        if (user != null) {
            try {
                cloudDataService.upsertProject(
                    userId = user.id,
                    projectId = projectId,
                    name = "",
                    patternId = null,
                    currentStepIndex = stepIndex,
                    progressPercent = progressPercent,
                    status = "active"
                )
            } catch (e: Exception) {
                Log.e(TAG, "updateProgress: Firestore write FAILED for $projectId", e)
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

    override suspend fun updateProjectSettings(
        projectId: String,
        name: String,
        colorHex: String?,
        selectedSize: String?,
        materialIds: List<String>
    ) {
        local.updateProjectSettings(projectId, name, colorHex, selectedSize, materialIds)
        val user = authService.currentUser
        if (user != null) {
            try {
                cloudDataService.upsertProject(
                    userId = user.id,
                    projectId = projectId,
                    name = name,
                    patternId = null,
                    currentStepIndex = 0,
                    progressPercent = 0,
                    status = "active"
                )
                materialIds.forEach { materialId ->
                    cloudDataService.upsertProjectMaterial(
                        userId = user.id,
                        id = "$projectId:$materialId",
                        projectId = projectId,
                        materialId = materialId,
                        plannedQuantity = null,
                        usedQuantity = null
                    )
                }
            } catch (e: Exception) {
                Log.e(TAG, "updateProjectSettings: Firestore write FAILED for $projectId", e)
            }
        }
    }

    override fun observeProjectMaterials(projectId: String): Flow<List<String>> {
        return local.observeProjectMaterials(projectId)
    }

    override suspend fun updateProjectColor(projectId: String, colorHex: String?) {
        local.updateProjectColor(projectId, colorHex)
        val user = authService.currentUser
        if (user != null) {
            try {
                cloudDataService.updateProjectColor(
                    userId = user.id,
                    projectId = projectId,
                    colorHex = colorHex
                )
            } catch (e: Exception) {
                Log.e(TAG, "updateProjectColor: Firestore write FAILED for $projectId", e)
            }
        }
    }
}
