package com.malha.app.domain.model

enum class CraftType {
    KNITTING, CROCHET
}

data class Gauge(
    val widthStitches: Int,
    val heightRows: Int,
    val measurementCm: Int = 10
)

data class Pattern(
    val id: String,
    val title: String,
    val craft: CraftType = CraftType.KNITTING,
    val difficulty: Int = 1, // 1 to 5
    val gauge: Gauge? = null,
    val sections: List<PatternSection> = emptyList()
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
    CAST_ON, RIBBING, INCREASE, DECREASE, BIND_OFF, NORMAL, REPEAT, FINISHING
}

data class PatternStep(
    val id: String,
    val orderIndex: Int,
    val type: StepType = StepType.NORMAL,
    val instruction: String,
    val rowNumber: Int? = null,
    val stitchCount: Int? = null,
    val confidence: Double = 1.0
)
