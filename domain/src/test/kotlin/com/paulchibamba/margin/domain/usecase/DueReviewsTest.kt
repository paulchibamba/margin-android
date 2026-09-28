package com.paulchibamba.margin.domain.usecase

import com.paulchibamba.margin.domain.feed.ConceptProgress
import com.paulchibamba.margin.domain.feed.cia
import com.paulchibamba.margin.domain.feed.introducedProgress
import com.paulchibamba.margin.domain.feed.leastPrivilege
import com.paulchibamba.margin.domain.feed.mcqOf
import com.paulchibamba.margin.domain.feed.now
import com.paulchibamba.margin.domain.feed.sameOrigin
import com.paulchibamba.margin.domain.feed.withIntroduced
import com.paulchibamba.margin.domain.memory.MemoryCard
import com.paulchibamba.margin.domain.signals.AnswerOutcome
import com.paulchibamba.margin.domain.signals.PostExit
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Duration
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds
import kotlin.time.toJavaDuration

class DueReviewsTest {

    private val fixture = UseCaseFixture()

    @Test
    fun `with nothing introduced nothing is due`() = runTest {
        assertEquals(0, fixture.countDueReviews())
    }

    @Test
    fun `concepts whose cards are due now or earlier are counted`() = runTest {
        fixture.progress.feedState.value = startingState()
            .withIntroduced(cia, progress = introducedProgress(due = now.minusSeconds(60)))
            .withIntroduced(leastPrivilege, progress = introducedProgress(due = now))
            .withIntroduced(sameOrigin, progress = introducedProgress(due = now.plusSeconds(60)))

        assertEquals(2, fixture.countDueReviews())
    }

    @Test
    fun `a concept answered right through its learning step today is due two days later`() = runTest {
        val newCard = ConceptProgress(introducedAtStep = 1, card = MemoryCard.new(now), lastShownStep = 1)
        fixture.progress.feedState.value = startingState().withIntroduced(cia, progress = newCard)
        answerRight()
        advanceClock(10.minutes)
        answerRight()

        advanceClock(1.days)
        assertEquals(0, fixture.countDueReviews())
        advanceClock(1.days)
        assertEquals(1, fixture.countDueReviews())
    }

    private suspend fun answerRight() {
        fixture.recordPostExit(mcqOf(cia), PostExit(5.seconds, true, AnswerOutcome.Correct))
    }

    private fun advanceClock(by: Duration) {
        fixture.clock.instant = fixture.clock.instant.plus(by.toJavaDuration())
    }

    private fun startingState() = fixture.engines.feedEngine(0.9).startingState()
}
