package com.malha.app.core.ai

class LocalAidiAssistantService : AidiAssistantService {
    override suspend fun generateReply(prompt: String): String {
        val normalized = prompt.lowercase()

        return when {
            "yarn" in normalized || "material" in normalized || "stash" in normalized -> {
                "Check your Materials tab first. If you already have a matching yarn weight and enough quantity, use that before buying more. For planning, add yarn weight, meters, and needle or hook size."
            }
            "gauge" in normalized || "tension" in normalized -> {
                "Make a small gauge swatch before adapting a pattern. Measure stitches and rows over 10 cm, then adjust stitch counts from that measured gauge instead of guessing."
            }
            "photo" in normalized || "image" in normalized || "picture" in normalized -> {
                "I can later help analyze photos as an experimental feature. For now, use photos as project references and verify all stitch counts manually."
            }
            "row" in normalized || "step" in normalized || "done" in normalized -> {
                "Open your project and use the step screen. Mark each step done and add notes when you change something, so the project stays easy to resume."
            }
            "help" in normalized || "k2tog" in normalized || "crochet" in normalized || "knit" in normalized -> {
                "Ask me about a technique, abbreviation, material choice, or project plan. I will give a cautious explanation and remind you when something needs manual checking."
            }
            else -> {
                "I can help plan projects, explain techniques, think through materials, and keep your progress organized. AI suggestions are drafts, so always verify stitch counts, gauge, and sizing."
            }
        }
    }
}

