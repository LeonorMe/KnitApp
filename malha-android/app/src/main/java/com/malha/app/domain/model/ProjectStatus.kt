package com.malha.app.domain.model

enum class ProjectStatus(val value: String) {
    DRAFT("draft"),
    READY("ready"),
    ACTIVE("active"),
    FINISHED("finished");

    companion object {
        fun from(value: String?): ProjectStatus = when (value?.lowercase()) {
            "draft" -> DRAFT
            "ready" -> READY
            "active", "in_progress" -> ACTIVE
            "finished", "completed" -> FINISHED
            else -> DRAFT
        }
    }
}
