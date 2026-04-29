package com.malha.app.domain.model

data class Material(
    val id: String,
    val name: String,
    val type: MaterialType,
    val quantity: Double,
    val unit: String
)

enum class MaterialType {
    Yarn,
    Needle,
    Hook,
    Accessory
}

