package com.malha.app.core.database.seed

import com.malha.app.core.database.dao.MaterialDao
import com.malha.app.core.database.dao.PatternDao
import com.malha.app.core.database.dao.ProjectDao
import com.malha.app.core.database.dao.SocialDao

class DatabaseSeeder(
    private val patternDao: PatternDao,
    private val projectDao: ProjectDao,
    private val materialDao: MaterialDao,
    private val socialDao: SocialDao
) {
    suspend fun seedIfEmpty() {
        if (patternDao.countPatterns() == 0) {
            patternDao.insertPatterns(SeedData.patterns)
            patternDao.insertSteps(SeedData.steps)
        }
        if (projectDao.countProjects() == 0) {
            projectDao.insertProjects(SeedData.projects)
        }
        if (materialDao.countMaterials() == 0) {
            materialDao.insertMaterials(SeedData.materials)
        }
        // Seed Social Data
        if (socialDao.getUser("default-user") == null) {
            for (user in SeedData.users) {
                socialDao.insertUser(user)
            }
            for (post in SeedData.posts) {
                socialDao.insertPost(post)
            }
            for (comment in SeedData.comments) {
                socialDao.insertComment(comment)
            }
        }
    }
}
