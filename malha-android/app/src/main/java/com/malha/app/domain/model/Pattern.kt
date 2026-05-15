package com.malha.app.domain.model

enum class CraftType {
    KNITTING, CROCHET
}

enum class SkillLevel {
    BEGINNER, EASY, INTERMEDIATE, ADVANCED, EXPERT
}

enum class SourceType {
    PDF, IMAGE, RAW_TEXT, MANUAL, COMMUNITY
}

enum class VerificationStatus {
    UNVERIFIED, AI_STRUCTURED, HUMAN_REVIEWED, COMMUNITY_VALIDATED
}

data class Gauge(
    val widthStitches: Int,
    val heightRows: Int,
    val measurementCm: Int = 10
)

data class Pattern(
    val id: String,
    val title: String,
    val designer: String? = null,
    val year: Int? = null,
    val craft: CraftType = CraftType.KNITTING,
    val difficulty: SkillLevel = SkillLevel.BEGINNER,
    val gauge: Gauge? = null,
    val sections: List<PatternSection> = emptyList(),
    
    // Lifecycle & Metadata
    val sourceType: SourceType = SourceType.MANUAL,
    val originalText: String? = null,
    val verificationStatus: VerificationStatus = VerificationStatus.UNVERIFIED,
    val aiConfidence: Double = 1.0,
    val isPremium: Boolean = false,
    
    // Multi-size
    val availableSizes: List<String> = emptyList(),
    val selectedSize: String? = null
) {
    val allSteps: List<PatternStep>
        get() = sections.flatMap { it.steps }.sortedBy { it.orderIndex }
}

data class PatternSection(
    val id: String,
    val name: String,
    val steps: List<PatternStep>
)

enum class StepType {
    CAST_ON, RIBBING, INCREASE, DECREASE, BIND_OFF, NORMAL, REPEAT, REPEAT_BLOCK, JOIN, FINISHING
}

data class PatternStep(
    val id: String,
    val orderIndex: Int,
    val type: StepType = StepType.NORMAL,
    val instruction: String,
    val rowNumber: Int? = null,
    val stitchCount: Int? = null,
    val confidence: Double = 1.0,
    
    // Execution Logic Parameters
    val repeatCount: Int? = null,
    val everyNRows: Int? = null,
    val startRow: Int? = null,
    val endRow: Int? = null,
    val condition: String? = null, // e.g. "size == XL"
    val stitchPatternId: String? = null // Reference to Stitch Library
)
