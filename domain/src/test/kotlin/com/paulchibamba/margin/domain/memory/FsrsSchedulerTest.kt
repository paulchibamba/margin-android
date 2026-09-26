package com.paulchibamba.margin.domain.memory

import java.time.Duration
import java.time.Instant
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.minutes

class FsrsSchedulerTest {

    private val now = Instant.parse("2026-10-01T08:00:00Z")
    private val scheduler = FsrsScheduler(FsrsParameters.Default, NoFuzz)

    @Test
    fun `an empty card is new and due immediately`() {
        val card = scheduler.createEmptyCard(now)

        assertEquals(CardState.NEW, card.state)
        assertEquals(now, card.due)
        assertEquals(0, card.reps)
    }

    @Test
    fun `a new card has no chance of recall yet`() {
        assertEquals(0.0, scheduler.retrievability(scheduler.createEmptyCard(now), now))
    }

    @Test
    fun `recall fades as days pass after a review`() {
        val reviewed = scheduler.next(scheduler.createEmptyCard(now), now, Rating.EASY)

        val soon = scheduler.retrievability(reviewed, now.plus(Duration.ofDays(1)))
        val later = scheduler.retrievability(reviewed, now.plus(Duration.ofDays(20)))

        assertTrue(later < soon, "expected $later < $soon")
    }

    @Test
    fun `a new card previews the learning steps and a first interval for easy`() {
        val intervals = scheduler.previewIntervals(scheduler.createEmptyCard(now), now)

        assertEquals(
            mapOf(Rating.AGAIN to 1.minutes, Rating.HARD to 6.minutes, Rating.GOOD to 10.minutes, Rating.EASY to 8.days),
            intervals,
        )
    }

    @Test
    fun `a preview promises exactly what grading then schedules, even with fuzz`() {
        val fuzzyScheduler = FsrsScheduler(FsrsParameters.Default, CardSeededFuzz)
        val card = cardInReviewWithLongStability(fuzzyScheduler)
        val reviewTime = card.due

        val preview = fuzzyScheduler.previewIntervals(card, reviewTime)

        Rating.entries.forEach { rating ->
            val due = fuzzyScheduler.next(card, reviewTime, rating).due
            assertEquals(preview.getValue(rating).inWholeMinutes, Duration.between(reviewTime, due).toMinutes())
        }
    }

    @Test
    fun `seeded fuzz keeps review intervals inside the ts-fsrs fuzz range`() {
        val fuzzyScheduler = FsrsScheduler(FsrsParameters.Default, CardSeededFuzz)
        val card = cardInReviewWithLongStability(fuzzyScheduler)
        val unfuzzed = scheduler.next(card, card.due, Rating.GOOD).scheduledDays
        val range = FuzzRange.of(unfuzzed, elapsedDays = card.scheduledDays, maximumInterval = 36500)

        val fuzzed = fuzzyScheduler.next(card, card.due, Rating.GOOD).scheduledDays

        assertTrue(fuzzed in range.shortest..range.longest, "$fuzzed outside $range (unfuzzed $unfuzzed)")
    }

    @Test
    fun `no fuzz always gives the same interval`() {
        val card = cardInReviewWithLongStability(scheduler)

        val intervals = List(5) { scheduler.next(card, card.due, Rating.GOOD).scheduledDays }

        assertEquals(1, intervals.distinct().size)
    }

    private fun cardInReviewWithLongStability(scheduler: FsrsScheduler): MemoryCard {
        var card = scheduler.next(scheduler.createEmptyCard(now), now, Rating.EASY)
        repeat(2) { card = scheduler.next(card, card.due, Rating.GOOD) }
        return card
    }
}
