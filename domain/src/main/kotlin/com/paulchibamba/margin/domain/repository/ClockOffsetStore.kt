package com.paulchibamba.margin.domain.repository

import kotlin.time.Duration

interface ClockOffsetStore {
    fun load(): Duration
    fun save(offset: Duration)
}
