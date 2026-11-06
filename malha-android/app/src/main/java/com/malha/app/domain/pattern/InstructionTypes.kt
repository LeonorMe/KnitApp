package com.malha.app.domain.pattern

enum class CountUnit {
    STITCH,
    ROW,
    REPEAT,
    ROUND,
    MOTIF,
    ACTION
}

enum class InstructionNodeType {
    COUNTED_ACTION,
    BINARY_ACTION,
    MEASUREMENT_CHECK,
    REPEAT_BLOCK,
    SECTION,
    NOTE,
    CONDITIONAL
}
