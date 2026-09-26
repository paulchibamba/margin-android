package com.paulchibamba.margin.domain.rewards

import com.paulchibamba.margin.domain.feed.ConceptProgress
import com.paulchibamba.margin.domain.feed.allConcepts
import com.paulchibamba.margin.domain.feed.appSec
import com.paulchibamba.margin.domain.feed.cia
import com.paulchibamba.margin.domain.feed.defenceInDepth
import com.paulchibamba.margin.domain.feed.introducedProgress
import com.paulchibamba.margin.domain.feed.leastPrivilege
import com.paulchibamba.margin.domain.feed.learnedCard
import com.paulchibamba.margin.domain.feed.readingOnlyIntro
import com.paulchibamba.margin.domain.memory.CardState
import com.paulchibamba.margin.domain.progression.ReadingOnlyChapters
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class BookCompletionCalculatorTest {

    private val calculator = BookCompletionCalculator(ReadingOnlyChapters(mapOf(appSec.slug to setOf(2))))

    private fun matureProgress(scheduledDays: Int) = ConceptProgress(
        introducedAtStep = 1,
        card = learnedCard().copy(state = CardState.REVIEW, scheduledDays = scheduledDays),
    )

    @Test
    fun `completion counts introduced concepts out of those outside reading-only chapters`() {
        val progress = mapOf(cia.id to introducedProgress(), readingOnlyIntro.id to introducedProgress())

        val completion = calculator.completionOf(appSec.slug, allConcepts, progress)

        assertEquals(BookCompletion(appSec.slug, introduced = 1, remembered = 0, total = 3), completion)
    }

    @Test
    fun `a concept is remembered only in review with 21 or more days scheduled`() {
        val progress = mapOf(
            cia.id to matureProgress(scheduledDays = 21),
            leastPrivilege.id to matureProgress(scheduledDays = 20),
            defenceInDepth.id to introducedProgress(),
        )

        assertEquals(1, calculator.completionOf(appSec.slug, allConcepts, progress).remembered)
    }

    @Test
    fun `a book is fully introduced and fully remembered when every counted concept is`() {
        val progress = listOf(cia, leastPrivilege, defenceInDepth).associate { it.id to matureProgress(30) }

        val completion = calculator.completionOf(appSec.slug, allConcepts, progress)

        assertTrue(completion.isFullyIntroduced)
        assertTrue(completion.isFullyRemembered)
    }
}
