package com.paulchibamba.margin.feature.bake

import java.time.LocalTime

interface BakeScheduler {
    fun scheduleDaily(at: LocalTime)
    fun rescheduleDaily(at: LocalTime)
    fun bakeAfterSession()
    fun bakeReExplainsNow()
}
