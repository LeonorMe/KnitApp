package com.malha.app.domain.pattern

import com.malha.app.domain.model.CraftType

enum class TechniqueSourceType {
    OFFICIAL,
    CUSTOM
}

data class Technique(
    val id: String,
    val craftType: CraftType,
    val canonicalNameKey: String, // String resource identifier or canonical key
    val descriptionKey: String? = null,
    val customName: String? = null,
    val customDescription: String? = null,
    val mediaUrl: String? = null,
    val sourceType: TechniqueSourceType = TechniqueSourceType.OFFICIAL,
    val ownerId: String? = null,
    val version: Int = 1,
    val isArchived: Boolean = false
) {
    /**
     * Normalized name for UI presentation: prefers custom name if set, else canonical key.
     */
    fun displayName(): String = customName ?: canonicalNameKey
}

data class TechniqueAlias(
    val id: String,
    val techniqueId: String,
    val normalizedAlias: String,
    val locale: String = "en",
    val displayAlias: String
) {
    companion object {
        /**
         * Normalizes aliases: lowercases, trims whitespace, removes diacritics/punctuation.
         */
        fun normalize(raw: String): String {
            val trimmed = raw.trim().lowercase()
            // Remove common diacritics
            val normalized = java.text.Normalizer.normalize(trimmed, java.text.Normalizer.Form.NFD)
            return normalized.replace(Regex("\\p{InCombiningDiacriticalMarks}+"), "")
                .replace(Regex("[^a-z0-9]+"), " ")
                .trim()
        }
    }
}
