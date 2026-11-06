package com.malha.app.domain.engine

import com.malha.app.domain.pattern.*
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class PatternExpansionEngineTest {

    @Test
    fun testStockinetteSquareExpansionTo22Instructions() {
        val patternId = "stockinette-square"
        val sizeKey = "10x10"
        val sectionId = "main"
        val repeatNodeId = "rep_body"

        // Build compressed 10x10 stockinette square definition per specification
        val version = PatternVersion(
            id = "v1_$patternId",
            patternId = patternId,
            versionNumber = 1,
            sizes = listOf(
                PatternSize(
                    id = "size_1",
                    patternVersionId = "v1_$patternId",
                    key = sizeKey,
                    displayName = "10 cm x 10 cm",
                    order = 0
                )
            ),
            parameters = listOf(
                PatternParameter(
                    id = "p1",
                    patternVersionId = "v1_$patternId",
                    sizeKey = sizeKey,
                    paramKey = "STITCH_COUNT",
                    value = "20",
                    unit = "stitches"
                ),
                PatternParameter(
                    id = "p2",
                    patternVersionId = "v1_$patternId",
                    sizeKey = sizeKey,
                    paramKey = "ROW_COUNT",
                    value = "20",
                    unit = "rows"
                )
            ),
            sections = listOf(
                CompressedSection(
                    id = sectionId,
                    patternVersionId = "v1_$patternId",
                    order = 0,
                    title = "Square",
                    nodes = listOf(
                        // 1. Cast-on 20
                        InstructionNode(
                            id = "cast_on",
                            sectionId = sectionId,
                            order = 0,
                            nodeType = InstructionNodeType.COUNTED_ACTION,
                            techniqueId = "cast-on",
                            targetParamKey = "STITCH_COUNT",
                            countUnit = CountUnit.STITCH
                        ),
                        // 2. Repeat 20 times alternating knit and purl
                        InstructionNode(
                            id = repeatNodeId,
                            sectionId = sectionId,
                            order = 1,
                            nodeType = InstructionNodeType.REPEAT_BLOCK,
                            repeatTimesParamKey = "ROW_COUNT",
                            childNodes = listOf(
                                InstructionNode(
                                    id = "knit_node",
                                    sectionId = sectionId,
                                    parentNodeId = repeatNodeId,
                                    order = 0,
                                    nodeType = InstructionNodeType.COUNTED_ACTION,
                                    techniqueId = "knit",
                                    targetParamKey = "STITCH_COUNT",
                                    countUnit = CountUnit.STITCH
                                ),
                                InstructionNode(
                                    id = "purl_node",
                                    sectionId = sectionId,
                                    parentNodeId = repeatNodeId,
                                    order = 1,
                                    nodeType = InstructionNodeType.COUNTED_ACTION,
                                    techniqueId = "purl",
                                    targetParamKey = "STITCH_COUNT",
                                    countUnit = CountUnit.STITCH
                                )
                            )
                        ),
                        // 3. Bind-off 20
                        InstructionNode(
                            id = "bind_off",
                            sectionId = sectionId,
                            order = 2,
                            nodeType = InstructionNodeType.COUNTED_ACTION,
                            techniqueId = "bind-off",
                            targetParamKey = "STITCH_COUNT",
                            countUnit = CountUnit.STITCH,
                            isDestructive = true
                        )
                    )
                )
            )
        )

        val snapshot = PatternExpansionEngine.expand(
            projectId = "test-project-123",
            version = version,
            sizeKey = sizeKey
        )

        // PASS when: The 10 cm x 10 cm example expands to 22 executable instructions
        assertEquals(22, snapshot.totalInstructionCount)
        assertEquals(22, snapshot.instructions.size)

        // Instruction 0: Cast on 20 stitches
        val first = snapshot.instructions[0]
        assertEquals(0, first.ordinal)
        assertEquals("cast-on", first.techniqueId)
        assertEquals(20, first.targetCount)
        assertEquals("Cast on 20 stitches", first.displayText)

        // Instruction 1: Row 1: Knit 20 stitches
        val row1 = snapshot.instructions[1]
        assertEquals(1, row1.ordinal)
        assertEquals("knit", row1.techniqueId)
        assertEquals(20, row1.targetCount)
        assertEquals("Row 1: Knit 20 stitches", row1.displayText)

        // Instruction 2: Row 2: Purl 20 stitches
        val row2 = snapshot.instructions[2]
        assertEquals(2, row2.ordinal)
        assertEquals("purl", row2.techniqueId)
        assertEquals(20, row2.targetCount)
        assertEquals("Row 2: Purl 20 stitches", row2.displayText)

        // Instruction 20: Row 20: Purl 20 stitches
        val row20 = snapshot.instructions[20]
        assertEquals(20, row20.ordinal)
        assertEquals("purl", row20.techniqueId)
        assertEquals(20, row20.targetCount)
        assertEquals("Row 20: Purl 20 stitches", row20.displayText)

        // Instruction 21: Bind off 20 stitches
        val last = snapshot.instructions[21]
        assertEquals(21, last.ordinal)
        assertEquals("bind-off", last.techniqueId)
        assertEquals(20, last.targetCount)
        assertEquals("Bind off 20 stitches", last.displayText)
        assertTrue(last.isDestructive)

        // Deterministic checksum exists
        assertTrue(snapshot.checksum.isNotBlank())
    }
}
