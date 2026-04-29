package com.malha.app.core.app

import android.content.Context
import com.malha.app.core.database.DatabaseProvider
import com.malha.app.core.database.seed.DatabaseSeeder
import com.malha.app.data.material.MaterialRepository
import com.malha.app.data.material.RoomMaterialRepository
import com.malha.app.data.pattern.PatternRepository
import com.malha.app.data.pattern.RoomPatternRepository
import com.malha.app.data.project.ProjectRepository
import com.malha.app.data.project.RoomProjectRepository

class AppContainer(context: Context) {
    private val database = DatabaseProvider.getDatabase(context)

    val projectRepository: ProjectRepository = RoomProjectRepository(database.projectDao())
    val patternRepository: PatternRepository = RoomPatternRepository(database.patternDao())
    val materialRepository: MaterialRepository = RoomMaterialRepository(database.materialDao())

    val databaseSeeder = DatabaseSeeder(
        patternDao = database.patternDao(),
        projectDao = database.projectDao(),
        materialDao = database.materialDao()
    )
}

