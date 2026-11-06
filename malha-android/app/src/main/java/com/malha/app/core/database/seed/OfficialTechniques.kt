package com.malha.app.core.database.seed

import com.malha.app.core.database.entity.TechniqueAliasEntity
import com.malha.app.core.database.entity.TechniqueEntity
import com.malha.app.domain.pattern.TechniqueAlias

object OfficialTechniques {

    val techniques = listOf(
        TechniqueEntity(
            id = "knit",
            craftType = "KNITTING",
            canonicalNameKey = "knit",
            descriptionKey = "technique_desc_knit",
            customName = "Knit",
            customDescription = "Insert right needle into front of stitch from left to right, wrap yarn counter-clockwise, and pull through to form a new stitch.",
            sourceType = "OFFICIAL",
            version = 1,
            isArchived = false
        ),
        TechniqueEntity(
            id = "purl",
            craftType = "KNITTING",
            canonicalNameKey = "purl",
            descriptionKey = "technique_desc_purl",
            customName = "Purl",
            customDescription = "Insert right needle from right to left into front of stitch with yarn in front, wrap yarn around, and push back through.",
            sourceType = "OFFICIAL",
            version = 1,
            isArchived = false
        ),
        TechniqueEntity(
            id = "cast-on",
            craftType = "KNITTING",
            canonicalNameKey = "cast-on",
            descriptionKey = "technique_desc_cast_on",
            customName = "Cast On",
            customDescription = "Create the foundation row of stitches onto the knitting needle to begin the project.",
            sourceType = "OFFICIAL",
            version = 1,
            isArchived = false
        ),
        TechniqueEntity(
            id = "bind-off",
            craftType = "KNITTING",
            canonicalNameKey = "bind-off",
            descriptionKey = "technique_desc_bind_off",
            customName = "Bind Off",
            customDescription = "Secure and lock the active stitches to finish the piece and prevent unraveling.",
            sourceType = "OFFICIAL",
            version = 1,
            isArchived = false
        ),
        TechniqueEntity(
            id = "yo",
            craftType = "KNITTING",
            canonicalNameKey = "yo",
            descriptionKey = "technique_desc_yo",
            customName = "Yarn Over",
            customDescription = "Wrap the working yarn over the right needle between two stitches to increase one stitch and create a decorative eyelet.",
            sourceType = "OFFICIAL",
            version = 1,
            isArchived = false
        ),
        TechniqueEntity(
            id = "k2tog",
            craftType = "KNITTING",
            canonicalNameKey = "k2tog",
            descriptionKey = "technique_desc_k2tog",
            customName = "Knit 2 Together",
            customDescription = "Insert right needle into two stitches simultaneously and knit them together to decrease by 1 stitch with a rightward slant.",
            sourceType = "OFFICIAL",
            version = 1,
            isArchived = false
        ),
        TechniqueEntity(
            id = "ssk",
            craftType = "KNITTING",
            canonicalNameKey = "ssk",
            descriptionKey = "technique_desc_ssk",
            customName = "Slip Slip Knit",
            customDescription = "Slip two stitches knitwise one at a time, insert left needle into the fronts of both stitches, and knit them together to decrease with a leftward slant.",
            sourceType = "OFFICIAL",
            version = 1,
            isArchived = false
        ),
        TechniqueEntity(
            id = "kfb",
            craftType = "KNITTING",
            canonicalNameKey = "kfb",
            descriptionKey = "technique_desc_kfb",
            customName = "Knit Front & Back",
            customDescription = "Knit into the front loop of a stitch, then before slipping it off, knit into the back loop to increase one stitch.",
            sourceType = "OFFICIAL",
            version = 1,
            isArchived = false
        ),
        TechniqueEntity(
            id = "sc",
            craftType = "CROCHET",
            canonicalNameKey = "sc",
            descriptionKey = "technique_desc_sc",
            customName = "Single Crochet",
            customDescription = "Insert hook into stitch, yarn over and pull up loop (2 loops on hook), yarn over and pull through both loops.",
            sourceType = "OFFICIAL",
            version = 1,
            isArchived = false
        ),
        TechniqueEntity(
            id = "dc",
            craftType = "CROCHET",
            canonicalNameKey = "dc",
            descriptionKey = "technique_desc_dc",
            customName = "Double Crochet",
            customDescription = "Yarn over, insert hook into stitch, pull up a loop (3 loops), yarn over and pull through 2 loops twice.",
            sourceType = "OFFICIAL",
            version = 1,
            isArchived = false
        ),
        TechniqueEntity(
            id = "ch",
            craftType = "CROCHET",
            canonicalNameKey = "ch",
            descriptionKey = "technique_desc_ch",
            customName = "Chain Stitch",
            customDescription = "Yarn over and pull through the active loop on hook to form foundation chains or turning chains.",
            sourceType = "OFFICIAL",
            version = 1,
            isArchived = false
        ),
        TechniqueEntity(
            id = "sl-st",
            craftType = "CROCHET",
            canonicalNameKey = "sl-st",
            descriptionKey = "technique_desc_sl_st",
            customName = "Slip Stitch",
            customDescription = "Insert hook into stitch, yarn over and pull directly through the stitch and the loop on the hook in one motion.",
            sourceType = "OFFICIAL",
            version = 1,
            isArchived = false
        )
    )

    val aliases = listOf(
        // Knit
        createAlias("knit", "k", "en", "k"),
        createAlias("knit", "knit", "en", "knit"),
        createAlias("knit", "trico", "pt", "tricô"),
        createAlias("knit", "ponto meia", "pt", "ponto meia"),
        createAlias("knit", "meia", "pt", "meia"),

        // Purl
        createAlias("purl", "p", "en", "p"),
        createAlias("purl", "purl", "en", "purl"),
        createAlias("purl", "liga", "pt", "liga"),
        createAlias("purl", "ponto liga", "pt", "ponto liga"),

        // Cast on
        createAlias("cast-on", "co", "en", "CO"),
        createAlias("cast-on", "cast on", "en", "cast on"),
        createAlias("cast-on", "montar pontos", "pt", "montar pontos"),
        createAlias("cast-on", "montar", "pt", "montar"),

        // Bind off
        createAlias("bind-off", "bo", "en", "BO"),
        createAlias("bind-off", "bind off", "en", "bind off"),
        createAlias("bind-off", "cast off", "en", "cast off"),
        createAlias("bind-off", "arrematar", "pt", "arrematar"),
        createAlias("bind-off", "rematar", "pt", "rematar"),

        // Yarn over
        createAlias("yo", "yo", "en", "yo"),
        createAlias("yo", "yarn over", "en", "yarn over"),
        createAlias("yo", "lacada", "pt", "laçada"),

        // K2tog
        createAlias("k2tog", "k2tog", "en", "k2tog"),
        createAlias("k2tog", "knit 2 together", "en", "knit 2 together"),
        createAlias("k2tog", "2pj", "pt", "2pj"),

        // SSK
        createAlias("ssk", "ssk", "en", "ssk"),
        createAlias("ssk", "slip slip knit", "en", "slip slip knit"),
        createAlias("ssk", "mate simples", "pt", "mate simples"),

        // Kfb
        createAlias("kfb", "kfb", "en", "kfb"),

        // Single Crochet
        createAlias("sc", "sc", "en", "sc"),
        createAlias("sc", "single crochet", "en", "single crochet"),
        createAlias("sc", "ponto baixo", "pt", "ponto baixo"),
        createAlias("sc", "pb", "pt", "pb"),

        // Double Crochet
        createAlias("dc", "dc", "en", "dc"),
        createAlias("dc", "double crochet", "en", "double crochet"),
        createAlias("dc", "ponto alto", "pt", "ponto alto"),
        createAlias("dc", "pa", "pt", "pa"),

        // Chain
        createAlias("ch", "ch", "en", "ch"),
        createAlias("ch", "chain", "en", "chain"),
        createAlias("ch", "correntinha", "pt", "correntinha"),
        createAlias("ch", "corr", "pt", "corr"),

        // Slip stitch
        createAlias("sl-st", "sl st", "en", "sl st"),
        createAlias("sl-st", "slip stitch", "en", "slip stitch"),
        createAlias("sl-st", "ponto baixissimo", "pt", "ponto baixíssimo"),
        createAlias("sl-st", "pbx", "pt", "pbx")
    )

    private fun createAlias(techniqueId: String, rawText: String, locale: String, display: String): TechniqueAliasEntity {
        return TechniqueAliasEntity(
            id = "${techniqueId}_${TechniqueAlias.normalize(rawText)}",
            techniqueId = techniqueId,
            normalizedAlias = TechniqueAlias.normalize(rawText),
            locale = locale,
            displayAlias = display
        )
    }
}
