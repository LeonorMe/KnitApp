package com.malha.app.domain.engine

import com.malha.app.domain.pattern.*
import org.junit.Assert.assertTrue
import org.junit.Test

class PatternValidatorTest {

    @Test
    fun testRejectsEmptySections() {
        val version = PatternVersion(
            id = "v1",
            patternId = "p1",
            sizes = listOf(PatternSize("s1", "v1", "M", "Medium")),
            sections = emptyList()
        )
        val result = PatternValidator.validatePatternVersion(version, "M")
        assertTrue(result is ValidationResult.Invalid)
    }

    @Test
    fun testRejectsMissingSize() {
        val version = PatternVersion(
            id = "v1",
            patternId = "p1",
            sizes = listOf(PatternSize("s1", "v1", "M", "Medium")),
            sections = listOf(CompressedSection("sec1", "v1", null, 0, "Main"))
        )
        val result = PatternValidator.validatePatternVersion(version, "XL")
        assertTrue(result is ValidationResult.Invalid)
    }

    @Test
    fun testRejectsRepeatCountZeroOrNegative() {
        val version = PatternVersion(
            id = "v1",
            patternId = "p1",
            sizes = listOf(PatternSize("s1", "v1", "M", "Medium")),
            sections = listOf(
                CompressedSection(
                    id = "sec1",
                    patternVersionId = "v1",
                    order = 0,
                    title = "Main",
                    nodes = listOf(
                        InstructionNode(
                            id = "rep1",
                            sectionId = "sec1",
                            order = 0,
                            nodeType = InstructionNodeType.REPEAT_BLOCK,
                            repeatTimesLiteral = 0,
                            childNodes = listOf(
                                InstructionNode("c1", "sec1", "rep1", 0, InstructionNodeType.COUNTED_ACTION, targetLiteral = 10)
                            )
                        )
                    )
                )
            )
        )
        val result = PatternValidator.validatePatternVersion(version, "M")
        assertTrue(result is ValidationResult.Invalid)
    }

    @Test
    fun testRejectsSafetyCeilingExceeded() {
        val version = PatternVersion(
            id = "v1",
            patternId = "p1",
            sizes = listOf(PatternSize("s1", "v1", "M", "Medium")),
            sections = listOf(
                CompressedSection(
                    id = "sec1",
                    patternVersionId = "v1",
                    order = 0,
                    title = "Main",
                    nodes = listOf(
                        InstructionNode(
                            id = "rep1",
                            sectionId = "sec1",
                            order = 0,
                            nodeType = InstructionNodeType.REPEAT_BLOCK,
                            repeatTimesLiteral = 15_000,
                            childNodes = listOf(
                                InstructionNode("c1", "sec1", "rep1", 0, InstructionNodeType.COUNTED_ACTION, targetLiteral = 10)
                            )
                        )
                    )
                )
            )
        )
        val result = PatternValidator.validatePatternVersion(version, "M")
        assertTrue(result is ValidationResult.Invalid)
    }
}
