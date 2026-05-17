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
    Yarn,
    Needle,
    Hook,
    Accessory,
    OtherAccessory;

    val storageValue: String
        get() = when (this) {
            Yarn -> "yarn"
            Needle -> "needle"
            Hook -> "hook"
            Accessory -> "accessory"
            OtherAccessory -> "other_accessory"
        }

    val label: String
        get() = when (this) {
            Yarn -> "Yarn"
            Needle -> "Needles"
            Hook -> "Hooks"
            Accessory -> "Accessories"
            OtherAccessory -> "Other"
        }

    companion object {
        fun fromStorageValue(value: String): MaterialType {
            return when (value) {
                "needle" -> Needle
                "hook" -> Hook
                "accessory" -> Accessory
                "other_accessory" -> OtherAccessory
                else -> Yarn
            }
        }
    }
}
