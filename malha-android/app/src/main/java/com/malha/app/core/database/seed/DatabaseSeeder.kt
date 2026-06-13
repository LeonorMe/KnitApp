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
    suspend fun seed() {
        seedIfEmpty()
        ensureDemoPatternBundles()
        ensureDemoMaterials()
    }

    suspend fun seedIfEmpty() {
        if (patternDao.countPatterns() == 0) {
            patternDao.insertPatterns(SeedData.patterns)
            patternDao.insertSections(SeedData.sections)
            patternDao.insertSteps(SeedData.steps)
        }
        if (projectDao.countProjects() == 0) {
            projectDao.insertProjects(SeedData.projects)
        }
        if (materialDao.countMaterials() == 0) {
            materialDao.insertMaterials(SeedData.materials)
        }
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

    /**
     * Upserts demo patterns that are required for manual testing.
     * Runs even when the database already has patterns, so installs that predated
     * newer seed data still receive full step graphs (e.g. square_purl_001).
     */
    private suspend fun ensureDemoPatternBundles() {
        SeedData.patterns.forEach { pattern ->
            val exists = patternDao.countPatternsById(pattern.id) > 0
            val sectionCount = patternDao.countSectionsForPattern(pattern.id)
            val stepCount = patternDao.countStepsForPattern(pattern.id)
            if (!exists || sectionCount == 0 || stepCount == 0) {
                patternDao.insertPattern(pattern)
                val sections = SeedData.sections.filter { it.patternId == pattern.id }
                val steps = SeedData.steps.filter { it.patternId == pattern.id }
                if (sections.isNotEmpty()) {
                    patternDao.insertSections(sections)
                }
                if (steps.isNotEmpty()) {
                    patternDao.insertSteps(steps)
                }
            }
        }
    }

    private suspend fun ensureDemoMaterials() {
        SeedData.demoMaterials.forEach { material ->
            if (materialDao.countMaterialsById(material.id) == 0) {
                materialDao.insertMaterial(material)
            }
        }
    }
}
