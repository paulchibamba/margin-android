package com.paulchibamba.margin.domain.tracking

fun interface EventRecorder {
    fun record(event: Event)
}
