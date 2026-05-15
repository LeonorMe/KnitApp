package com.malha.app.core.ai

data class ParsedPattern(
    val title: String,
    val steps: List<ParsedStep>
)

data class ParsedStep(
    val rowNumber: Int?,
    val instruction: String
)

object PatternParser {
    /**
     * A simple heuristic parser to convert raw text into a structured pattern.
     * In a production app, this would be handled by a more robust LLM call.
     */
    fun parse(text: String): ParsedPattern {
        val lines = text.lines().filter { it.isNotBlank() }
        val title = lines.firstOrNull()?.take(50) ?: "Imported Pattern"
        val steps = mutableListOf<ParsedStep>()
        
        lines.forEach { line ->
            // Look for "Row X:" or "Rnd X:" or similar
            val rowMatch = Regex("""(?:Row|Rnd|Carrera)\s*(\d+)""", RegexOption.IGNORE_CASE).find(line)
            val rowNumber = rowMatch?.groupValues?.getOrNull(1)?.toIntOrNull()
            
            if (rowNumber != null || line.length > 5) {
                steps.add(ParsedStep(rowNumber, line.trim()))
            }
        }
        
        return ParsedPattern(title, steps)
    }
}
