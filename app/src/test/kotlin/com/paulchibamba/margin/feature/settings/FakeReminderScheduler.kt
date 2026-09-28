package com.paulchibamba.margin.feature.settings

import com.paulchibamba.margin.feature.reminder.ReminderScheduler

class FakeReminderScheduler : ReminderScheduler {
    var isScheduled = false
        private set

    override fun schedule() {
        isScheduled = true
    }

    override fun cancel() {
        isScheduled = false
    }
}
