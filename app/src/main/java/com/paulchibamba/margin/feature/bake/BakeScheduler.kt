package com.paulchibamba.margin.feature.bake

interface BakeScheduler {
    fun scheduleDaily()
    fun bakeAfterSession()
    fun bakeReExplainsNow()
}
