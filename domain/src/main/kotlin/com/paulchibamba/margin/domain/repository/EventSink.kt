package com.paulchibamba.margin.domain.repository

import com.paulchibamba.margin.domain.tracking.LoggedEvent

interface EventSink {
    fun append(event: LoggedEvent)
    suspend fun flush()
}
