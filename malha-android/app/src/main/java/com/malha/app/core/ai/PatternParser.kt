package com.malha.app.core.ai

import com.malha.app.domain.model.Pattern
import com.malha.app.domain.model.PatternSection
import com.malha.app.domain.model.PatternStep
import com.malha.app.domain.model.StepType
import java.util.UUID

object PatternParser {
    /**
     * A heuristic parser to convert raw text into a structured pattern with sections and step types.
     */
    fun parse(text: String): Pattern {
        val lines = text.lines().map { it.trim() }.filter { it.isNotBlank() }
        val title = lines.firstOrNull()?.take(50) ?: "Imported Pattern"
        
        val sections = mutableListOf<PatternSection>()
        var currentSectionSteps = mutableListOf<PatternStep>()
        var currentSectionName = "Introduction"
        var stepCount = 0
        
        lines.drop(1).forEach { line ->
            // Heuristic for section header: all caps, or ends with colon, or specific keywords
            val isSectionHeader = (line.length < 30 && line.all { it.isUpperCase() || !it.isLetter() || it.isWhitespace() }) || 
                                 (line.endsWith(":") && line.length < 40) || 
                                 listOf("FRONT", "BACK", "SLEEVE", "NECK", "YOKE", "CUFF", "RIB").any { line.contains(it, ignoreCase = true) && line.length < 20 }
            
            if (isSectionHeader && line.length > 2) {
                if (currentSectionSteps.isNotEmpty()) {
                    sections.add(PatternSection(UUID.randomUUID().toString(), currentSectionName, currentSectionSteps.toList()))
                    currentSectionSteps.clear()
                }
                currentSectionName = line.removeSuffix(":")
            } else {
                val stepType = inferStepType(line)
                val rowNumber = extractRowNumber(line)
                val stitchCount = extractStitchCount(line)
                
                currentSectionSteps.add(
                    PatternStep(
                        id = UUID.randomUUID().toString(),
                        orderIndex = stepCount++,
                        type = stepType,
                        instruction = line,
                        rowNumber = rowNumber,
                        stitchCount = stitchCount,
                        confidence = 0.85
                    )
                )
            }
        }
        
        if (currentSectionSteps.isNotEmpty() || sections.isEmpty()) {
            sections.add(PatternSection(UUID.randomUUID().toString(), currentSectionName, currentSectionSteps.toList()))
        }
        
        return Pattern(
            id = UUID.randomUUID().toString(),
            title = title,
            sections = sections
        )
    }

    private fun inferStepType(line: String): StepType {
        val l = line.lowercase()
        return when {
            l.contains("cast on") || l.contains("montar") -> StepType.CAST_ON
            l.contains("rib") || l.contains("canelado") -> StepType.RIBBING
            l.contains("inc") || l.contains("aumentar") || l.contains("aum.") -> StepType.INCREASE
            l.contains("dec") || l.contains("diminuir") || l.contains("dim.") -> StepType.DECREASE
            l.contains("bind off") || l.contains("rematar") -> StepType.BIND_OFF
            l.contains("repeat") || l.contains("repetir") -> StepType.REPEAT
            l.contains("sew") || l.contains("finish") || l.contains("coser") -> StepType.FINISHING
            else -> StepType.NORMAL
        }
    }

    private fun extractRowNumber(line: String): Int? {
        val rowMatch = Regex("""(?:Row|Rnd|Carrera|Volta)\s*(\d+)""", RegexOption.IGNORE_CASE).find(line)
        return rowMatch?.groupValues?.getOrNull(1)?.toIntOrNull()
    }

    private fun extractStitchCount(line: String): Int? {
        // Look for numbers near "sts", "stitches", "malhas", "m." at the end of the line
        val stsMatch = Regex("""(\d+)\s*(?:sts|stitches|malhas|m\.)""", RegexOption.IGNORE_CASE).find(line)
        return stsMatch?.groupValues?.getOrNull(1)?.toIntOrNull()
    }
}
