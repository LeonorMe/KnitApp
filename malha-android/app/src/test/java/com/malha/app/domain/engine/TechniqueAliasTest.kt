package com.malha.app.domain.engine

import com.malha.app.domain.pattern.TechniqueAlias
import org.junit.Assert.assertEquals
import org.junit.Test

class TechniqueAliasTest {

    @Test
    fun testNormalizeRemovesAccentsAndPunctuation() {
        assertEquals("trico", TechniqueAlias.normalize("Tricô!"))
        assertEquals("ponto meia", TechniqueAlias.normalize("  Ponto Meia  "))
        assertEquals("lacada", TechniqueAlias.normalize("Laçada"))
        assertEquals("2pj", TechniqueAlias.normalize("2PJ"))
        assertEquals("cast on", TechniqueAlias.normalize("Cast-On"))
    }
}
