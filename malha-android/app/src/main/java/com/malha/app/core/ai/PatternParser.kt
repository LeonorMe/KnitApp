package com.malha.app.core.ai

import com.malha.app.domain.model.*
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
                val repeatCount = extractRepeatCount(line)
                
                currentSectionSteps.add(
                    PatternStep(
                        id = UUID.randomUUID().toString(),
                        orderIndex = stepCount++,
                        type = stepType,
                        instructionData = line,
                        rowNumber = rowNumber,
                        stitchCountData = stitchCount?.toString(),
                        confidence = 0.85,
                        estimatedRounds = repeatCount
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
            sections = sections,
            sourceType = SourceType.RAW_TEXT,
            originalText = text,
            verificationStatus = VerificationStatus.AI_STRUCTURED,
            aiConfidence = 0.82
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
            l.contains("repeat rows") || l.contains("repetir as carreiras") -> StepType.REPEAT_BLOCK
            l.contains("repeat") || l.contains("repetir") -> StepType.REPEAT
            l.contains("join") || l.contains("unir") -> StepType.JOIN
            l.contains("sew") || l.contains("finish") || l.contains("coser") -> StepType.FINISHING
            else -> StepType.NORMAL
        }
    }

    private fun extractRowNumber(line: String): Int? {
        val rowMatch = Regex("""(?:Row|Rnd|Carrera|Volta)\s*(\d+)""", RegexOption.IGNORE_CASE).find(line)
        return rowMatch?.groupValues?.getOrNull(1)?.toIntOrNull()
    }

    private fun extractStitchCount(line: String): Int? {
        val stsMatch = Regex("""(\d+)\s*(?:sts|stitches|malhas|m\.)""", RegexOption.IGNORE_CASE).find(line)
        return stsMatch?.groupValues?.getOrNull(1)?.toIntOrNull()
    }

    private fun extractRepeatCount(line: String): Int? {
        val repMatch = Regex("""(\d+)\s*(?:times|vezes)""", RegexOption.IGNORE_CASE).find(line)
        return repMatch?.groupValues?.getOrNull(1)?.toIntOrNull()
    }
}
