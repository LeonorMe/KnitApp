package com.malha.app.core.app

import android.content.Context
import androidx.lifecycle.AndroidViewModel
import com.malha.app.BuildConfig
import com.malha.app.MalhaApplication
import com.malha.app.core.ai.AidiAssistantService
import com.malha.app.core.ai.CloudAidiAssistantService
import com.malha.app.core.ai.LocalAidiAssistantService
import com.malha.app.core.database.DatabaseProvider
import com.malha.app.core.database.seed.DatabaseSeeder
import com.malha.app.core.firebase.AuthService
import com.malha.app.core.firebase.CloudDataService
import com.malha.app.core.firebase.FirebaseAuthService
import com.malha.app.core.firebase.FirebaseCloudDataService
import com.malha.app.data.material.CloudBackedMaterialRepository
import com.malha.app.data.material.MaterialRepository
import com.malha.app.data.material.RoomMaterialRepository
import com.malha.app.data.pattern.PatternRepository
import com.malha.app.data.pattern.RoomPatternRepository
import com.malha.app.data.project.CloudBackedProjectRepository
import com.malha.app.data.project.ProjectRepository
import com.malha.app.data.project.RoomProjectRepository
import com.malha.app.core.preferences.PreferencesRepository
import com.malha.app.data.social.CloudBackedSocialRepository
import com.malha.app.data.social.RoomSocialRepository
import com.malha.app.data.social.SocialRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.Dispatchers

class AppContainer(context: Context) {
    private val database = DatabaseProvider.getDatabase(context)

    val authService: AuthService = FirebaseAuthService()
    val cloudDataService: CloudDataService = FirebaseCloudDataService()

    private val localProjectRepository: ProjectRepository = RoomProjectRepository(
        projectDao = database.projectDao(),
        projectStepProgressDao = database.projectStepProgressDao()
    )
    val projectRepository: ProjectRepository = CloudBackedProjectRepository(
        local = localProjectRepository,
        authService = authService,
        cloudDataService = cloudDataService
    )

    val patternRepository: PatternRepository = RoomPatternRepository(database.patternDao())

    private val localMaterialRepository: MaterialRepository = RoomMaterialRepository(database.materialDao())
    val materialRepository: MaterialRepository = CloudBackedMaterialRepository(
        local = localMaterialRepository,
        authService = authService,
        cloudDataService = cloudDataService
    )

    /**
     * Aidi assistant — uses the Anthropic cloud API when a key is available,
     * otherwise falls back to the local rule-based engine so the app works offline.
     */
    val aidiAssistantService: AidiAssistantService = run {
        val apiKey = try { BuildConfig.ANTHROPIC_API_KEY } catch (_: Throwable) { "" }
        if (apiKey.isNotBlank()) {
            CloudAidiAssistantService(apiKey = apiKey)
        } else {
            LocalAidiAssistantService()
        }
    }

    val preferencesRepository = PreferencesRepository(context)

    val databaseSeeder = DatabaseSeeder(
        patternDao = database.patternDao(),
        projectDao = database.projectDao(),
        materialDao = database.materialDao(),
        socialDao = database.socialDao()
    )

    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val localSocialRepository: SocialRepository =
        RoomSocialRepository(
            socialDao = database.socialDao(),
            authService = authService
        )

    val socialRepository: SocialRepository =
        CloudBackedSocialRepository(
            local = localSocialRepository,
            authService = authService,
            cloudDataService = cloudDataService,
            patternRepository = patternRepository,
            syncScope = applicationScope
        )
}

val Context.appContainer: AppContainer
    get() = (applicationContext as MalhaApplication).appContainer

val AndroidViewModel.appContainer: AppContainer
    get() = (getApplication<MalhaApplication>()).appContainer
