package com.malha.app.domain.pattern

import com.malha.app.domain.model.CraftType
import com.malha.app.domain.model.SourceType

data class PatternDefinition(
    val id: String,
    val ownerId: String? = null,
    val title: String,
    val craftType: CraftType = CraftType.KNITTING,
    val currentVersionId: String? = null,
    val sourceType: SourceType = SourceType.MANUAL,
    val isArchived: Boolean = false,
    val versions: List<PatternVersion> = emptyList()
)

data class PatternVersion(
    val id: String,
    val patternId: String,
    val versionNumber: Int = 1,
    val schemaVersion: Int = 1,
    val status: String = "PUBLISHED", // DRAFT, REVIEW, PUBLISHED, ARCHIVED
    val createdAt: Long = System.currentTimeMillis(),
    val checksum: String = "",
    val sizes: List<PatternSize> = emptyList(),
    val parameters: List<PatternParameter> = emptyList(),
    val sections: List<CompressedSection> = emptyList()
)

data class PatternSize(
    val id: String,
    val patternVersionId: String,
    val key: String, // e.g. "S", "M", "L", "10x10"
    val displayName: String, // e.g. "Small (80-85cm)", "10 cm x 10 cm"
    val order: Int = 0,
    val measurementsJson: String? = null
)

data class PatternParameter(
    val id: String,
    val patternVersionId: String,
    val sizeKey: String, // size identifier this value applies to
    val paramKey: String, // e.g. "STITCH_COUNT", "ROW_COUNT", "BODY_STITCHES"
    val value: String, // stored as string e.g. "20", "36"
    val unit: String? = null // e.g. "stitches", "rows", "cm"
)

data class CompressedSection(
    val id: String,
    val patternVersionId: String,
    val parentSectionId: String? = null,
    val order: Int,
    val title: String,
    val nodes: List<InstructionNode> = emptyList()
)

data class InstructionNode(
    val id: String,
    val sectionId: String,
    val parentNodeId: String? = null,
    val order: Int,
    val nodeType: InstructionNodeType,
    val techniqueId: String? = null,
    val targetLiteral: Int? = null,
    val targetParamKey: String? = null,
    val countUnit: CountUnit = CountUnit.STITCH,
    val repeatTimesLiteral: Int? = null,
    val repeatTimesParamKey: String? = null,
    val displayText: String? = null,
    val measurementValue: Float? = null,
    val measurementUnit: String? = null,
    val isDestructive: Boolean = false,
    val childNodes: List<InstructionNode> = emptyList()
)
