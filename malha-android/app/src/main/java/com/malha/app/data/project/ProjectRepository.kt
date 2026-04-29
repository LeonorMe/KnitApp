package com.malha.app.data.project

import com.malha.app.domain.model.Project

interface ProjectRepository {
    suspend fun getProjects(): List<Project>
}

