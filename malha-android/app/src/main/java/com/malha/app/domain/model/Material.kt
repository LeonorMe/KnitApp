package com.malha.app.domain.model

data class Material(
    val id: String,
    val name: String,
    val imageUri: String?,
    val type: MaterialType,
    val quantity: Double,
    val unit: String,
    
    // Structured Data for Yarn & Needles
    val fiber: String? = null,
    val weightCategory: String? = null,
    val gramsPerBall: Int? = null,
    val needleType: String? = null, // circular, straight, DPN
    val sizeMm: Double? = null,
    val lengthCm: Int? = null
)

enum class MaterialType {
    Pattern,
    Yarn,
    Needle,
    Hook, // todo remove
    Tool,
    OtherAccessory;

    val storageValue: String // todo string
        get() = when (this) {
            Pattern -> "pattern"
            Yarn -> "yarn"
            Needle -> "needle"
            Hook -> "hook"
            Tool -> "tool"
            OtherAccessory -> "other_accessory"
        }

    val label: String
        get() = when (this) {
            Pattern -> "Pattern" // todo translate
            Yarn -> "Yarn"
            Needle -> "Needles"
            Hook -> "Hooks"
            Tool -> "Tools"
            OtherAccessory -> "Other"
        }

    companion object {
        fun fromStorageValue(value: String): MaterialType {
            return when (value) {
                "pattern" -> Pattern
                "needle" -> Needle
                "hook" -> Hook
                "tool" -> Tool
                "other_accessory" -> OtherAccessory
                else -> Yarn
            }
        }
    }
}
