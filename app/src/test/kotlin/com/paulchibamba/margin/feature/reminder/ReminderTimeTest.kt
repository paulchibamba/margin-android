package com.paulchibamba.margin.feature.reminder

import org.junit.Test
import java.time.Duration
import java.time.ZonedDateTime
import kotlin.test.assertEquals

class ReminderTimeTest {

    @Test
    fun `before the evening the reminder comes the same day`() {
        val now = ZonedDateTime.parse("2026-10-01T08:30:00+02:00[Africa/Lusaka]")

        assertEquals(Duration.ofMinutes(630), ReminderTime.delayUntilNext(ReminderTime.EVENING, now))
    }

    @Test
    fun `at or after the evening the reminder comes the next day`() {
        val now = ZonedDateTime.parse("2026-10-01T19:00:00+02:00[Africa/Lusaka]")

        assertEquals(Duration.ofHours(24), ReminderTime.delayUntilNext(ReminderTime.EVENING, now))
    }
}
