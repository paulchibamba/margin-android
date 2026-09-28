package com.paulchibamba.margin.feature.reminder

import com.paulchibamba.margin.domain.usecase.ReviewReminder
import org.junit.Test
import kotlin.test.assertEquals

class ReviewReminderLabelsTest {

    @Test
    fun `the notification reads as in the design`() {
        val reminder = ReviewReminder(dueCount = 5, streak = 7)

        assertEquals("5 reviews are due", ReviewReminderLabels.title(reminder))
        assertEquals("Keep your 7-day streak. About 2 minutes.", ReviewReminderLabels.text(reminder))
    }

    @Test
    fun `one review and no streak read in the singular`() {
        val reminder = ReviewReminder(dueCount = 1, streak = 0)

        assertEquals("1 review is due", ReviewReminderLabels.title(reminder))
        assertEquals("Start a streak today. About 1 minute.", ReviewReminderLabels.text(reminder))
    }
}
