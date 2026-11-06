package com.malha.app.domain.engine

import org.junit.Assert.assertEquals
import org.junit.Test

class ProgressCalculatorTest {

    @Test
    fun testProgressCalculationRule() {
        // PASS when: After 11 of 22 instructions are complete, project progress displays 50%
        assertEquals(50, ProgressCalculator.calculatePercentage(11, 22))

        // 0% at start
        assertEquals(0, ProgressCalculator.calculatePercentage(0, 22))

        // 100% when all complete
        assertEquals(100, ProgressCalculator.calculatePercentage(22, 22))

        // Integer floor rule: 1 of 3 is floor(33.333%) = 33%
        assertEquals(33, ProgressCalculator.calculatePercentage(1, 3))

        // 2 of 3 is floor(66.666%) = 66%
        assertEquals(66, ProgressCalculator.calculatePercentage(2, 3))

        // Edge case: 0 total steps
        assertEquals(0, ProgressCalculator.calculatePercentage(0, 0))

        // Clamping overflow
        assertEquals(100, ProgressCalculator.calculatePercentage(25, 22))
    }
}
