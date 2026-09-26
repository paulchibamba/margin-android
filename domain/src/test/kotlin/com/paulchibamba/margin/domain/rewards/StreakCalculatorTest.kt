package com.paulchibamba.margin.domain.rewards

import com.paulchibamba.margin.domain.rewards.StreakDayStatus.DONE
import com.paulchibamba.margin.domain.rewards.StreakDayStatus.MISSED
import com.paulchibamba.margin.domain.rewards.StreakDayStatus.PENDING
import com.paulchibamba.margin.domain.rewards.StreakDayStatus.TODAY
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class StreakCalculatorTest {

    private val calculator = StreakCalculator(ZoneId.of("Africa/Lusaka"))
    private val thursday = LocalDate.of(2026, 10, 1)

    private fun daysBefore(today: LocalDate, vararg offsets: Long) =
        offsets.map { offset -> DailyActivity(today.minusDays(offset), postsSeen = 5) }

    @Test
    fun `consecutive counted days make the streak`() {
        assertEquals(3, calculator.currentStreak(daysBefore(thursday, 0, 1, 2), thursday))
    }

    @Test
    fun `a missed day resets the streak`() {
        assertEquals(1, calculator.currentStreak(daysBefore(thursday, 0, 2, 3, 4), thursday))
    }

    @Test
    fun `today still in progress keeps yesterday's count`() {
        val activities = daysBefore(thursday, 1, 2) + DailyActivity(thursday, postsSeen = 2)

        assertEquals(2, calculator.currentStreak(activities, thursday))
    }

    @Test
    fun `with no counted day yesterday or today there is no streak`() {
        assertEquals(0, calculator.currentStreak(daysBefore(thursday, 2, 3), thursday))
    }

    @Test
    fun `a day counts with 5 posts or 1 note, not fewer`() {
        val rule = StreakRule()

        assertTrue(rule.countsTowardStreak(DailyActivity(thursday, postsSeen = 5)))
        assertTrue(rule.countsTowardStreak(DailyActivity(thursday, notesRead = 1)))
        assertFalse(rule.countsTowardStreak(DailyActivity(thursday, postsSeen = 4, notesRead = 0)))
        assertFalse(rule.countsTowardStreak(null))
    }

    @Test
    fun `the week strip runs Monday to Sunday around a mid-week day`() {
        val activities = daysBefore(thursday, 1, 3) + DailyActivity(thursday, postsSeen = 1)

        val strip = calculator.weekStrip(activities, thursday)

        assertEquals(LocalDate.of(2026, 9, 28), strip.first().date)
        assertEquals(LocalDate.of(2026, 10, 4), strip.last().date)
        assertEquals(listOf(DONE, MISSED, DONE, TODAY, PENDING, PENDING, PENDING), strip.map { it.status })
    }

    @Test
    fun `today shows as done once it counts`() {
        val strip = calculator.weekStrip(daysBefore(thursday, 0), thursday)

        assertEquals(DONE, strip.single { it.date == thursday }.status)
    }

    @Test
    fun `the streak is extended by the post that makes today count`() {
        val fourPosts = DailyActivity(thursday, postsSeen = 4)
        val fivePosts = fourPosts.copy(postsSeen = 5)
        val sixPosts = fourPosts.copy(postsSeen = 6)

        assertTrue(calculator.isExtendedBy(fourPosts, fivePosts))
        assertTrue(calculator.isExtendedBy(null, DailyActivity(thursday, notesRead = 1)))
        assertFalse(calculator.isExtendedBy(fivePosts, sixPosts))
        assertFalse(calculator.isExtendedBy(null, fourPosts))
    }

    @Test
    fun `today is the calendar date in the user's zone`() {
        val lateUtcEvening = Instant.parse("2026-09-30T23:30:00Z")

        assertEquals(thursday, calculator.today(lateUtcEvening))
    }
}
