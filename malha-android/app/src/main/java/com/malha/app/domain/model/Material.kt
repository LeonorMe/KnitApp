package com.malha.app.domain.model

data class Material(
    val id: String,
    val name: String,
    val imageUri: String?,
    val type: MaterialType,
    val quantity: Double,
    val unit: String
)

enum class MaterialType {
    Yarn,
    Needle,
    Hook,
    Accessory;

    val storageValue: String
        get() = when (this) {
            Yarn -> "yarn"
            Needle -> "needle"
            Hook -> "hook"
            Accessory -> "accessory"
        }

    val label: String
        get() = when (this) {
            Yarn -> "Yarn"
            Needle -> "Needles"
            Hook -> "Hooks"
            Accessory -> "Accessories"
        }

    companion object {
        fun fromStorageValue(value: String): MaterialType {
            return when (value) {
                "needle" -> Needle
                "hook" -> Hook
                "accessory" -> Accessory
                else -> Yarn
            }
        }
    }
}
