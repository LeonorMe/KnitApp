package com.malha.app.domain.validation

object ProjectStartValidator {
    data class ValidationResult(
        val isReady: Boolean,
        val missingRequirements: List<String>
    )

    fun validate(
        patternId: String?,
        selectedSize: String?,
        selectedYarnId: String?,
        selectedNeedleId: String?
    ): ValidationResult {
        val missing = mutableListOf<String>()
        if (patternId.isNullOrBlank()) {
            missing.add("Select a pattern")
        }
        if (selectedSize.isNullOrBlank()) {
            missing.add("Select a size")
        }
        if (selectedYarnId.isNullOrBlank() && selectedNeedleId.isNullOrBlank()) {
            missing.add("Select materials")
        }

        return ValidationResult(
            isReady = missing.isEmpty(),
            missingRequirements = missing
        )
    }
}
