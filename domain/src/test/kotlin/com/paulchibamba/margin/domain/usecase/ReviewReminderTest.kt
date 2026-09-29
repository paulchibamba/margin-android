package com.paulchibamba.margin.domain.usecase

import com.paulchibamba.margin.domain.feed.cia
import com.paulchibamba.margin.domain.feed.introducedProgress
import com.paulchibamba.margin.domain.feed.leastPrivilege
import com.paulchibamba.margin.domain.feed.now
import com.paulchibamba.margin.domain.feed.withIntroduced
import com.paulchibamba.margin.domain.rewards.DailyActivity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import java.time.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlin.time.Duration.Companion.minutes

class ReviewReminderTest {

    private val fixture = UseCaseFixture()
    private val today = LocalDate.parse("2026-10-01")

    @Test
    fun `with reviews due and today's streak not yet kept the reminder counts them and the streak`() = runTest {
        remindersOn()
        twoConceptsDue()
        fixture.progress.activity.value = listOf(keptOn(today.minusDays(2)), keptOn(today.minusDays(1)))

        assertEquals(ReviewReminder(dueCount = 2, streak = 2), fixture.getReviewReminder())
    }

    @Test
    fun `there is no reminder when nothing is due`() = runTest {
        remindersOn()

        assertNull(fixture.getReviewReminder())
    }

    @Test
    fun `there is no reminder once today's streak is kept`() = runTest {
        remindersOn()
        twoConceptsDue()
        fixture.progress.activity.value = listOf(keptOn(today))

        assertNull(fixture.getReviewReminder())
    }

    @Test
    fun `a day with a few posts but not five still gets a reminder`() = runTest {
        remindersOn()
        twoConceptsDue()
        fixture.progress.activity.value = listOf(DailyActivity(today, postsSeen = 4))

        assertEquals(0, fixture.getReviewReminder()?.streak)
    }

    @Test
    fun `there is no reminder while the setting is off`() = runTest {
        twoConceptsDue()

        assertNull(fixture.getReviewReminder())
    }

    @Test
    fun `the setting is off until turned on and shows in the learning settings`() = runTest {
        assertFalse(fixture.observeLearningSettings().first().isReviewReminderOn)

        fixture.setReviewReminder(true)

        assertTrue(fixture.observeLearningSettings().first().isReviewReminderOn)
    }

    @Test
    fun `reviews take about 25 seconds each, rounded to whole minutes and at least one`() {
        assertEquals(1.minutes, ReviewReminder(dueCount = 1, streak = 0).reviewTime)
        assertEquals(2.minutes, ReviewReminder(dueCount = 5, streak = 7).reviewTime)
        assertEquals(8.minutes, ReviewReminder(dueCount = 20, streak = 7).reviewTime)
    }

    private suspend fun remindersOn() = fixture.setReviewReminder(true)

    private fun twoConceptsDue() {
        fixture.progress.feedState.value = fixture.engines.feedEngine(0.9).startingState()
            .withIntroduced(cia, leastPrivilege, progress = introducedProgress(due = now.minusSeconds(60)))
    }

    private fun keptOn(day: LocalDate) = DailyActivity(day, postsSeen = 5)
}
