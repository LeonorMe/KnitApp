package com.malha.app.domain.engine

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class IncrementSuggestionPolicyTest {

    @Test
    fun testSuggestIncrementsForTarget36() {
        val increments = IncrementSuggestionPolicy.suggestIncrements(36)
        // PASS when: A counted target of 36 can be completed using +1, +6, +12, or another valid custom increment
        assertTrue(increments.contains(1))
        assertTrue(increments.contains(2) || increments.contains(3))
        assertTrue(increments.contains(6))
        assertTrue(increments.contains(12))
    }

    @Test
    fun testSuggestIncrementsForTarget20() {
        val increments = IncrementSuggestionPolicy.suggestIncrements(20)
        assertTrue(increments.contains(1))
        assertTrue(increments.contains(2))
        assertTrue(increments.contains(5) || increments.contains(10))
    }

    @Test
    fun testNextCountClampsToTarget() {
        // PASS when: Never allow counter to exceed target; final tap clamps to target
        val current = 34
        val increment = 6
        val target = 36
        val next = IncrementSuggestionPolicy.nextCount(current, increment, target)
        assertEquals(36, next)
    }

    @Test
    fun testPreviousCountDoesNotGoBelowZero() {
        val current = 2
        val decrement = 6
        val prev = IncrementSuggestionPolicy.previousCount(current, decrement)
        assertEquals(0, prev)
    }
}
