package com.malha.app.domain.engine

object IncrementSuggestionPolicy {

    /**
     * Given a target count, returns a list of convenient suggested increment values.
     * Always includes +1.
     * For target 36: returns e.g. [1, 2, 6, 12].
     * For target 20: returns e.g. [1, 2, 5, 10].
     * For target 10: returns [1, 2, 5].
     */
    fun suggestIncrements(target: Int): List<Int> {
        if (target <= 1) return listOf(1)

        val divisors = mutableListOf<Int>()
        for (i in 1 until target) {
            if (target % i == 0) {
                divisors.add(i)
            }
        }

        val priorityDivisors = listOf(1, 2, 5, 6, 10, 12, 3, 4, 8, 9, 16, 18)
        val suggestions = mutableListOf<Int>()
        for (p in priorityDivisors) {
            if (p == 1 || divisors.contains(p)) {
                suggestions.add(p)
            }
            if (suggestions.size >= 4) break
        }

        // If no preferred divisor found (e.g. large prime or unusual target), offer 1 and 5/10
        if (suggestions.size == 1 && target > 10) {
            suggestions.add(5)
            if (target > 20) suggestions.add(10)
        }

        return suggestions.distinct().sorted()
    }

    /**
     * Clamps the next counter value so it never exceeds target.
     */
    fun nextCount(current: Int, increment: Int, target: Int): Int {
        if (increment <= 0) return current
        return minOf(current + increment, target)
    }

    /**
     * Decrements the counter value safely, not going below zero.
     */
    fun previousCount(current: Int, decrement: Int): Int {
        if (decrement <= 0) return current
        return maxOf(current - decrement, 0)
    }
}
