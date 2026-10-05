package com.paulchibamba.margin.domain.tracking

interface SessionListener {
    fun eventsAtStart(): List<Event>
    fun eventsAtEnd(): List<Event>
}
