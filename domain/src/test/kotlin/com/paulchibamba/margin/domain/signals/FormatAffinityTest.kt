package com.paulchibamba.margin.domain.signals

import com.paulchibamba.margin.domain.model.Format
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Duration.Companion.milliseconds

class FormatAffinityTest {

    private val neutral = FormatAffinity()

    @Test
    fun `every format starts neutral`() {
        assertEquals(0.5, neutral.valueOf(Format.MYTH))
    }

    @Test
    fun `a fast skip on a checklist moves its affinity from half to three eighths`() {
        val fastSkip = PostExit(dwell = 500.milliseconds, isEngaged = false)
        val engagement = EngagementCalculator().score(checklistWithWords(10), fastSkip)

        val affinity = neutral.afterEngagement(Format.CHECKLIST, engagement)

        assertEquals(0.375, affinity.valueOf(Format.CHECKLIST), TOLERANCE)
    }

    @Test
    fun `affinity moves a quarter of the way toward each engagement`() {
        val affinity = neutral.afterEngagement(Format.MCQ, engagement = 1.0)

        assertEquals(0.625, affinity.valueOf(Format.MCQ), TOLERANCE)
    }

    @Test
    fun `less halves a format's affinity`() {
        assertEquals(0.25, neutral.afterLess(Format.MEME).valueOf(Format.MEME), TOLERANCE)
    }

    @Test
    fun `changing one format leaves the others and the original untouched`() {
        val changed = neutral.afterLess(Format.MEME)

        assertEquals(0.5, changed.valueOf(Format.TIP))
        assertEquals(0.5, neutral.valueOf(Format.MEME))
    }

    private companion object {
        const val TOLERANCE = 1e-9
    }
}
