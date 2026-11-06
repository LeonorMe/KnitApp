package com.malha.app.domain.engine

import com.malha.app.domain.pattern.CompressedSection
import com.malha.app.domain.pattern.CountUnit
import com.malha.app.domain.pattern.InstructionNode
import com.malha.app.domain.pattern.InstructionNodeType
import com.malha.app.domain.pattern.PatternVersion

sealed class ValidationResult {
    object Valid : ValidationResult()
    data class Invalid(val errors: List<String>) : ValidationResult()
}

object PatternValidator {
    const val MAX_EXPANDED_STEPS_CEILING = 10_000

    fun validatePatternVersion(version: PatternVersion, sizeKey: String): ValidationResult {
        val errors = mutableListOf<String>()

        if (version.sections.isEmpty()) {
            errors.add("Pattern version must contain at least one section.")
        }

        val size = version.sizes.firstOrNull { it.key == sizeKey }
        if (size == null) {
            errors.add("Size '$sizeKey' is not defined in pattern version.")
        }

        // Build parameter lookup for the given size
        val paramsForSize = version.parameters
            .filter { it.sizeKey == sizeKey }
            .associateBy { it.paramKey }

        var estimatedStepCount = 0

        fun validateNode(node: InstructionNode, path: String, depth: Int) {
            if (depth > 10) {
                errors.add("Safety ceiling exceeded: instruction hierarchy too deep at $path.")
                return
            }

            when (node.nodeType) {
                InstructionNodeType.COUNTED_ACTION -> {
                    val target = resolveTarget(node, paramsForSize)
                    if (target == null) {
                        errors.add("Counted step at $path has missing or unresolved target parameter '${node.targetParamKey}'.")
                    } else if (target <= 0) {
                        errors.add("Counted step at $path target must be greater than zero, found $target.")
                    }
                    estimatedStepCount++
                }
                InstructionNodeType.BINARY_ACTION -> {
                    if (node.displayText.isNullOrBlank() && node.techniqueId.isNullOrBlank()) {
                        errors.add("Binary step at $path must have display text or a technique reference.")
                    }
                    estimatedStepCount++
                }
                InstructionNodeType.MEASUREMENT_CHECK -> {
                    if (node.measurementValue == null || node.measurementValue <= 0f) {
                        errors.add("Measurement check at $path requires a positive target measurement.")
                    }
                    estimatedStepCount++
                }
                InstructionNodeType.REPEAT_BLOCK -> {
                    val repeatTimes = resolveRepeatTimes(node, paramsForSize)
                    if (repeatTimes == null) {
                        errors.add("Repeat block at $path has missing or unresolved repeat count parameter '${node.repeatTimesParamKey}'.")
                    } else if (repeatTimes <= 0) {
                        errors.add("Repeat block at $path repeat count must be greater than zero, found $repeatTimes.")
                    } else {
                        if (node.childNodes.isEmpty()) {
                            errors.add("Repeat block at $path must contain at least one child instruction.")
                        } else {
                            val cycleSize = node.childNodes.size
                            estimatedStepCount += repeatTimes * cycleSize
                            if (estimatedStepCount > MAX_EXPANDED_STEPS_CEILING) {
                                errors.add("Pattern exceeds maximum safety ceiling of $MAX_EXPANDED_STEPS_CEILING expanded steps.")
                                return
                            }
                            node.childNodes.forEachIndexed { i, child ->
                                validateNode(child, "$path/child-$i", depth + 1)
                            }
                        }
                    }
                }
                InstructionNodeType.SECTION,
                InstructionNodeType.NOTE,
                InstructionNodeType.CONDITIONAL -> {
                    // Non-executable in flat stream or handled via cycle
                }
            }
        }

        for (section in version.sections) {
            if (section.title.isBlank()) {
                errors.add("Section '${section.id}' has blank title.")
            }
            section.nodes.forEachIndexed { index, node ->
                validateNode(node, "section/${section.id}/node-$index", 1)
            }
        }

        return if (errors.isEmpty()) ValidationResult.Valid else ValidationResult.Invalid(errors)
    }

    private fun resolveTarget(node: InstructionNode, params: Map<String, com.malha.app.domain.pattern.PatternParameter>): Int? {
        if (node.targetLiteral != null) return node.targetLiteral
        val paramKey = node.targetParamKey ?: return null
        return params[paramKey]?.value?.toIntOrNull()
    }

    private fun resolveRepeatTimes(node: InstructionNode, params: Map<String, com.malha.app.domain.pattern.PatternParameter>): Int? {
        if (node.repeatTimesLiteral != null) return node.repeatTimesLiteral
        val paramKey = node.repeatTimesParamKey ?: return null
        return params[paramKey]?.value?.toIntOrNull()
    }
}
