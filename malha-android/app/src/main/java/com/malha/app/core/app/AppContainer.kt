package com.malha.app.core.app

import android.content.Context
import com.malha.app.core.ai.AidiAssistantService
import com.malha.app.core.ai.LocalAidiAssistantService
import com.malha.app.core.database.DatabaseProvider
import com.malha.app.core.database.seed.DatabaseSeeder
import com.malha.app.core.firebase.AuthService
import com.malha.app.core.firebase.CloudDataService
import com.malha.app.core.firebase.FirebaseAuthService
import com.malha.app.core.firebase.FirestoreCloudDataService
import com.malha.app.data.material.MaterialRepository
import com.malha.app.data.material.RoomMaterialRepository
import com.malha.app.data.pattern.PatternRepository
import com.malha.app.data.pattern.RoomPatternRepository
import com.malha.app.data.project.ProjectRepository
import com.malha.app.data.project.RoomProjectRepository
import com.malha.app.core.preferences.PreferencesRepository
import com.malha.app.data.social.RoomSocialRepository
import com.malha.app.data.social.SocialRepository

class AppContainer(context: Context) {
    private val database = DatabaseProvider.getDatabase(context)

    val projectRepository: ProjectRepository = RoomProjectRepository(
        projectDao = database.projectDao(),
        projectStepProgressDao = database.projectStepProgressDao()
    )
    val patternRepository: PatternRepository = RoomPatternRepository(database.patternDao())
    val materialRepository: MaterialRepository = RoomMaterialRepository(database.materialDao())
    val authService: AuthService = FirebaseAuthService()
    val cloudDataService: CloudDataService = FirestoreCloudDataService()
    val aidiAssistantService: AidiAssistantService = LocalAidiAssistantService()
    val preferencesRepository = PreferencesRepository(context)
    val socialRepository: SocialRepository = RoomSocialRepository(database.socialDao())

    val databaseSeeder = DatabaseSeeder(
        patternDao = database.patternDao(),
        projectDao = database.projectDao(),
        materialDao = database.materialDao(),
        socialDao = database.socialDao()
    )
}

val android.content.Context.appContainer: AppContainer
    get() = (applicationContext as com.malha.app.MalhaApplication).appContainer

val androidx.lifecycle.AndroidViewModel.appContainer: AppContainer
    get() = (getApplication<com.malha.app.MalhaApplication>()).appContainer
