package com.malha.app.domain.engine

import com.malha.app.domain.pattern.CompressedSection
import com.malha.app.domain.pattern.CountUnit
import com.malha.app.domain.pattern.InstructionNode
import com.malha.app.domain.pattern.InstructionNodeType
import com.malha.app.domain.pattern.PatternParameter
import com.malha.app.domain.pattern.PatternVersion
import com.malha.app.domain.pattern.ProjectInstructionSnapshot
import com.malha.app.domain.pattern.ProjectPatternSnapshot
import java.security.MessageDigest
import java.util.UUID

object PatternExpansionEngine {

    /**
     * Deterministically expands a compressed PatternVersion for a selected size into an immutable ProjectPatternSnapshot.
     */
    fun expand(
        projectId: String,
        version: PatternVersion,
        sizeKey: String
    ): ProjectPatternSnapshot {
        val validation = PatternValidator.validatePatternVersion(version, sizeKey)
        if (validation is ValidationResult.Invalid) {
            throw IllegalArgumentException("Cannot expand pattern: ${validation.errors.joinToString("; ")}")
        }

        val paramsForSize = version.parameters
            .filter { it.sizeKey == sizeKey }
            .associateBy { it.paramKey }

        val snapshotId = UUID.randomUUID().toString()
        val expandedList = mutableListOf<ProjectInstructionSnapshot>()
        var currentOrdinal = 0
        var globalRowCount = 0

        for (section in version.sections) {
            for (node in section.nodes) {
                expandNode(
                    node = node,
                    section = section,
                    params = paramsForSize,
                    snapshotId = snapshotId,
                    parentPath = "section/${section.id}",
                    expandedOut = expandedList,
                    ordinalProvider = { currentOrdinal++ },
                    rowCounterProvider = { ++globalRowCount }
                )
            }
        }

        val checksum = calculateChecksum(expandedList)

        return ProjectPatternSnapshot(
            id = snapshotId,
            projectId = projectId,
            sourcePatternId = version.patternId,
            sourceVersionId = version.id,
            selectedSizeKey = sizeKey,
            schemaVersion = version.schemaVersion,
            checksum = checksum,
            totalInstructionCount = expandedList.size,
            instructions = expandedList
        )
    }

    private fun expandNode(
        node: InstructionNode,
        section: CompressedSection,
        params: Map<String, PatternParameter>,
        snapshotId: String,
        parentPath: String,
        expandedOut: MutableList<ProjectInstructionSnapshot>,
        ordinalProvider: () -> Int,
        rowCounterProvider: () -> Int
    ) {
        val currentPath = "$parentPath/node/${node.id}"

        when (node.nodeType) {
            InstructionNodeType.COUNTED_ACTION -> {
                val target = resolveTarget(node, params) ?: 1
                val ordinal = ordinalProvider()
                val display = buildDisplayText(
                    node = node,
                    target = target,
                    rowNumber = null
                )

                expandedOut.add(
                    ProjectInstructionSnapshot(
                        id = UUID.randomUUID().toString(),
                        snapshotId = snapshotId,
                        ordinal = ordinal,
                        stablePath = currentPath,
                        type = node.nodeType,
                        techniqueId = node.techniqueId,
                        targetCount = target,
                        countUnit = node.countUnit,
                        sectionTitle = section.title,
                        displayText = display,
                        measurementValue = node.measurementValue,
                        measurementUnit = node.measurementUnit,
                        isDestructive = node.isDestructive
                    )
                )
            }

            InstructionNodeType.BINARY_ACTION,
            InstructionNodeType.MEASUREMENT_CHECK -> {
                val ordinal = ordinalProvider()
                val display = node.displayText ?: when (node.nodeType) {
                    InstructionNodeType.MEASUREMENT_CHECK ->
                        "Work until piece measures ${node.measurementValue ?: 0f} ${node.measurementUnit ?: "cm"}"
                    else -> node.techniqueId ?: "Perform action"
                }

                expandedOut.add(
                    ProjectInstructionSnapshot(
                        id = UUID.randomUUID().toString(),
                        snapshotId = snapshotId,
                        ordinal = ordinal,
                        stablePath = currentPath,
                        type = node.nodeType,
                        techniqueId = node.techniqueId,
                        targetCount = null,
                        countUnit = node.countUnit,
                        sectionTitle = section.title,
                        displayText = display,
                        measurementValue = node.measurementValue,
                        measurementUnit = node.measurementUnit,
                        isDestructive = node.isDestructive
                    )
                )
            }

            InstructionNodeType.REPEAT_BLOCK -> {
                val repeatTimes = resolveRepeatTimes(node, params) ?: 0
                val children = node.childNodes
                if (children.isEmpty() || repeatTimes <= 0) return

                val cycleSize = children.size
                for (repIndex in 0 until repeatTimes) {
                    val childIndex = repIndex % cycleSize
                    val childNode = children[childIndex]
                    val repPath = "$currentPath/rep-${String.format("%02d", repIndex + 1)}"
                    val rowNum = rowCounterProvider()

                    val target = resolveTarget(childNode, params) ?: 1
                    val ordinal = ordinalProvider()
                    val display = buildDisplayText(
                        node = childNode,
                        target = target,
                        rowNumber = rowNum
                    )

                    expandedOut.add(
                        ProjectInstructionSnapshot(
                            id = UUID.randomUUID().toString(),
                            snapshotId = snapshotId,
                            ordinal = ordinal,
                            stablePath = "$repPath/child-${childNode.id}",
                            type = childNode.nodeType,
                            techniqueId = childNode.techniqueId,
                            targetCount = target,
                            countUnit = childNode.countUnit,
                            sectionTitle = section.title,
                            displayText = display,
                            measurementValue = childNode.measurementValue,
                            measurementUnit = childNode.measurementUnit,
                            isDestructive = childNode.isDestructive
                        )
                    )
                }
            }

            InstructionNodeType.SECTION,
            InstructionNodeType.NOTE,
            InstructionNodeType.CONDITIONAL -> {
                // Non-executable, skipped in flat execution instructions
            }
        }
    }

    private fun buildDisplayText(node: InstructionNode, target: Int, rowNumber: Int?): String {
        if (!node.displayText.isNullOrBlank()) {
            return if (rowNumber != null) "Row $rowNumber: ${node.displayText}" else node.displayText
        }

        val unitName = when (node.countUnit) {
            CountUnit.STITCH -> if (target == 1) "stitch" else "stitches"
            CountUnit.ROW -> if (target == 1) "row" else "rows"
            CountUnit.ROUND -> if (target == 1) "round" else "rounds"
            CountUnit.REPEAT -> if (target == 1) "repeat" else "repeats"
            CountUnit.MOTIF -> if (target == 1) "motif" else "motifs"
            CountUnit.ACTION -> if (target == 1) "time" else "times"
        }

        val techName = when (node.techniqueId) {
            "cast-on" -> "Cast on"
            "bind-off" -> "Bind off"
            "knit" -> "Knit"
            "purl" -> "Purl"
            "sc" -> "Single crochet"
            "dc" -> "Double crochet"
            "ch" -> "Chain"
            null -> "Work"
            else -> node.techniqueId.replace("-", " ").replaceFirstChar { it.uppercase() }
        }

        return if (rowNumber != null) {
            "Row $rowNumber: $techName $target $unitName"
        } else {
            "$techName $target $unitName"
        }
    }

    private fun resolveTarget(node: InstructionNode, params: Map<String, PatternParameter>): Int? {
        if (node.targetLiteral != null) return node.targetLiteral
        val paramKey = node.targetParamKey ?: return null
        return params[paramKey]?.value?.toIntOrNull()
    }

    private fun resolveRepeatTimes(node: InstructionNode, params: Map<String, PatternParameter>): Int? {
        if (node.repeatTimesLiteral != null) return node.repeatTimesLiteral
        val paramKey = node.repeatTimesParamKey ?: return null
        return params[paramKey]?.value?.toIntOrNull()
    }

    private fun calculateChecksum(instructions: List<ProjectInstructionSnapshot>): String {
        val digest = MessageDigest.getInstance("SHA-256")
        for (inst in instructions) {
            val s = "${inst.ordinal}:${inst.stablePath}:${inst.targetCount}:${inst.techniqueId}:${inst.displayText}\n"
            digest.update(s.toByteArray(Charsets.UTF_8))
        }
        val bytes = digest.digest()
        return bytes.joinToString("") { "%02x".format(it) }
    }
}
