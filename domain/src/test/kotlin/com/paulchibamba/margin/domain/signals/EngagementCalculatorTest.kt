package com.paulchibamba.margin.domain.signals

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

class EngagementCalculatorTest {

    private val calculator = EngagementCalculator()
    private val fiveSecondTip = tipWithWords(20)
    private val fiveSecondChecklist = checklistWithWords(20)

    @Test
    fun `leaving quickly without interacting shows no interest`() {
        val exit = PostExit(dwell = 1100.milliseconds, isEngaged = false)

        assertEquals(0.0, calculator.score(fiveSecondChecklist, exit))
    }

    @Test
    fun `interacting counts even when the post was left quickly`() {
        val exit = PostExit(dwell = 1.seconds, isEngaged = true)

        assertEquals(0.6 * 0.2 + 0.4, calculator.score(fiveSecondChecklist, exit), TOLERANCE)
    }

    @Test
    fun `a read-only post scores the share of its expected reading time`() {
        val exit = PostExit(dwell = 2.seconds, isEngaged = false)

        assertEquals(0.4, calculator.score(fiveSecondTip, exit), TOLERANCE)
    }

    @Test
    fun `an interactive post blends reading time with interaction`() {
        val exit = PostExit(dwell = 2.5.seconds, isEngaged = true)

        assertEquals(0.6 * 0.5 + 0.4, calculator.score(fiveSecondChecklist, exit), TOLERANCE)
    }

    @Test
    fun `an interactive post read without interacting scores reading time only`() {
        val exit = PostExit(dwell = 5.seconds, isEngaged = false)

        assertEquals(0.6, calculator.score(fiveSecondChecklist, exit), TOLERANCE)
    }

    @Test
    fun `lingering longer than expected counts as fully read, not more`() {
        val exit = PostExit(dwell = 60.seconds, isEngaged = false)

        assertEquals(1.0, calculator.score(fiveSecondTip, exit))
    }

    private companion object {
        const val TOLERANCE = 1e-9
    }
}
