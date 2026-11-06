package com.malha.app.domain.engine

enum class PatternEditDecision {
    CREATE_COPY,
    UPDATE_SOURCE,
    CANCEL
}

data class PatternUsageInfo(
    val patternId: String,
    val activeProjectCount: Int,
    val draftProjectCount: Int
) {
    val isInUse: Boolean get() = activeProjectCount > 0 || draftProjectCount > 0
}
