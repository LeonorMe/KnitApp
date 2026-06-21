package com.malha.app.data.pattern

import com.malha.app.core.database.dao.PatternDao
import com.malha.app.core.database.entity.PatternEntity
import com.malha.app.core.database.entity.PatternSectionEntity
import com.malha.app.core.database.entity.PatternStepEntity
import com.malha.app.core.firebase.AuthService
import com.malha.app.core.firebase.CloudDataService
import com.malha.app.domain.model.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.UUID

class CloudBackedPatternRepository(
    private val local: PatternRepository,
    private val patternDao: PatternDao,
    private val authService: AuthService,
    private val cloudDataService: CloudDataService,
    private val syncScope: CoroutineScope
) : PatternRepository by local {

    init {
        // Continuous Firestore -> Room sync for Patterns
        syncScope.launch {
            cloudDataService.observePatterns()
                .collectLatest { remotePatterns ->
                    remotePatterns.forEach { map ->
                        val id = map["id"] as? String ?: return@forEach
                        val title = map["title"] as? String ?: return@forEach
                        val designer = map["designer"] as? String
                        val year = (map["year"] as? Long)?.toInt()
                        val craftStr = map["craft"] as? String ?: "KNITTING"
                        val diffStr = map["difficulty"] as? String ?: "BEGINNER"
                        val sourceStr = map["sourceType"] as? String ?: "MANUAL"
                        val verStr = map["verificationStatus"] as? String ?: "UNVERIFIED"
                        
                        val craft = runCatching { CraftType.valueOf(craftStr) }.getOrDefault(CraftType.KNITTING)
                        val difficulty = runCatching { SkillLevel.valueOf(diffStr) }.getOrDefault(SkillLevel.BEGINNER)
                        val sourceType = runCatching { SourceType.valueOf(sourceStr) }.getOrDefault(SourceType.MANUAL)
                        val verificationStatus = runCatching { VerificationStatus.valueOf(verStr) }.getOrDefault(VerificationStatus.UNVERIFIED)

                        val gaugeWidth = (map["gaugeWidth"] as? Long)?.toInt()
                        val gaugeHeight = (map["gaugeHeight"] as? Long)?.toInt()
                        val gaugeUnit = map["gaugeUnit"] as? String
                        val originalText = map["originalText"] as? String
                        val aiConfidence = (map["aiConfidence"] as? Double) ?: 1.0
                        val isPremium = (map["isPremium"] as? Boolean) ?: false
                        val availableSizes = map["availableSizes"] as? String
                        val selectedSize = map["selectedSize"] as? String
                        val updatedAt = (map["updatedAt"] as? Long) ?: System.currentTimeMillis()

                        patternDao.insertPattern(
                            PatternEntity(
                                id = id,
                                title = title,
                                designer = designer,
                                year = year,
                                craft = craft,
                                difficulty = difficulty,
                                gaugeWidth = gaugeWidth,
                                gaugeHeight = gaugeHeight,
                                gaugeUnit = gaugeUnit,
                                sourceType = sourceType,
                                originalText = originalText,
                                verificationStatus = verificationStatus,
                                aiConfidence = aiConfidence,
                                isPremium = isPremium,
                                availableSizes = availableSizes,
                                selectedSize = selectedSize,
                                updatedAt = updatedAt
                            )
                        )
                    }
                }
        }

        // Continuous Firestore -> Room sync for Pattern Steps
        syncScope.launch {
            cloudDataService.observePatternSteps()
                .collectLatest { remoteSteps ->
                    remoteSteps.forEach { map ->
                        val id = map["id"] as? String ?: return@forEach
                        val patternId = map["patternId"] as? String ?: return@forEach
                        val sectionId = map["sectionId"] as? String ?: return@forEach
                        val orderIndex = (map["orderIndex"] as? Long)?.toInt() ?: 0
                        val stepTypeStr = map["stepType"] as? String ?: "NORMAL"
                        val stepType = runCatching { StepType.valueOf(stepTypeStr) }.getOrDefault(StepType.NORMAL)
                        val instruction = map["instruction"] as? String ?: ""
                        val rowNumber = (map["rowNumber"] as? Long)?.toInt()
                        val stitchCount = (map["stitchCount"] as? Long)?.toInt()
                        val confidence = (map["confidence"] as? Double) ?: 1.0
                        
                        val repeatCount = (map["repeatCount"] as? Long)?.toInt()
                        val everyNRows = (map["everyNRows"] as? Long)?.toInt()
                        val startRow = (map["startRow"] as? Long)?.toInt()
                        val endRow = (map["endRow"] as? Long)?.toInt()
                        val condition = map["condition"] as? String
                        val stitchPatternId = map["stitchPatternId"] as? String
                        val updatedAt = (map["updatedAt"] as? Long) ?: System.currentTimeMillis()

                        patternDao.insertStep(
                            PatternStepEntity(
                                id = id,
                                patternId = patternId,
                                sectionId = sectionId,
                                orderIndex = orderIndex,
                                stepType = stepType,
                                instruction = instruction,
                                rowNumber = rowNumber,
                                stitchCount = stitchCount,
                                confidence = confidence,
                                repeatCount = repeatCount,
                                everyNRows = everyNRows,
                                startRow = startRow,
                                endRow = endRow,
                                condition = condition,
                                stitchPatternId = stitchPatternId,
                                updatedAt = updatedAt
                            )
                        )
                    }
                }
        }
    }

    override suspend fun createManualPattern(title: String, instructions: List<String>) {
        // Create locally first
        local.createManualPattern(title, instructions)

        // Then upload pattern, section, and steps to Firestore
        val authUser = authService.currentUser
        if (authUser != null) {
            runCatching {
                // To get the IDs and structure created locally, we fetch the pattern we just created.
                val firstPatternId = local.getFirstPatternId() ?: return@runCatching
                val pattern = local.getPattern(firstPatternId) ?: return@runCatching

                // Upsert pattern
                cloudDataService.upsertPattern(
                    patternId = pattern.id,
                    title = pattern.title,
                    designer = pattern.designer,
                    year = pattern.year,
                    craft = pattern.craft.name,
                    difficulty = pattern.difficulty.name,
                    gaugeWidth = pattern.gauge?.widthStitches,
                    gaugeHeight = pattern.gauge?.heightRows,
                    gaugeUnit = pattern.gauge?.measurementCm?.let { "${it}cm" },
                    sourceType = pattern.sourceType.name,
                    originalText = pattern.originalText,
                    verificationStatus = pattern.verificationStatus.name,
                    aiConfidence = pattern.aiConfidence,
                    isPremium = pattern.isPremium,
                    availableSizes = pattern.availableSizes.takeIf { it.isNotEmpty() }?.joinToString(","),
                    selectedSize = pattern.selectedSize,
                    updatedAt = System.currentTimeMillis()
                )

                // Upsert steps
                pattern.sections.forEach { section ->
                    section.steps.forEach { step ->
                        cloudDataService.upsertPatternStep(
                            stepId = step.id,
                            patternId = pattern.id,
                            sectionId = section.id,
                            orderIndex = step.orderIndex,
                            stepType = step.type.name,
                            instruction = step.instructionData,
                            rowNumber = step.rowNumber,
                            stitchCount = step.stitchCountData?.toIntOrNull(),
                            confidence = step.confidence,
                            repeatCount = null,
                            everyNRows = null,
                            startRow = null,
                            endRow = null,
                            condition = step.condition,
                            stitchPatternId = step.stitchPatternId,
                            updatedAt = System.currentTimeMillis()
                        )
                    }
                }
            }
        }
    }
}