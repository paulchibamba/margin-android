package com.paulchibamba.margin.feature.reminder

import com.paulchibamba.margin.domain.usecase.ReviewReminder

object ReviewReminderLabels {

    fun title(reminder: ReviewReminder): String =
        if (reminder.dueCount == 1) "1 review is due" else "${reminder.dueCount} reviews are due"

    fun text(reminder: ReviewReminder): String = "${streakLine(reminder.streak)} ${timeLine(reminder)}"

    private fun streakLine(streak: Int): String =
        if (streak == 0) "Start a streak today." else "Keep your $streak-day streak."

    private fun timeLine(reminder: ReviewReminder): String {
        val minutes = reminder.reviewTime.inWholeMinutes
        return if (minutes == 1L) "About 1 minute." else "About $minutes minutes."
    }
}
