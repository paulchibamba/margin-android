package com.paulchibamba.margin.domain.tracking

import com.paulchibamba.margin.domain.repository.EventSink

class RecordingEventSink : EventSink {
    val pending = mutableListOf<LoggedEvent>()
    val flushed = mutableListOf<LoggedEvent>()
    var flushCount = 0
        private set

    val all: List<LoggedEvent> get() = flushed + pending

    override fun append(event: LoggedEvent) {
        pending += event
    }

    override suspend fun flush() {
        flushCount++
        flushed += pending
        pending.clear()
    }
}
