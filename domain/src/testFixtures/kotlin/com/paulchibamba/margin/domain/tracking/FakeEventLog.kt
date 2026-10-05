package com.paulchibamba.margin.domain.tracking

import com.paulchibamba.margin.domain.repository.EventLog

class FakeEventLog : EventLog {
    var unfinished: UnfinishedSession? = null
    var recentEvents: List<RecentEvent> = emptyList()
    val counts = mutableMapOf<Pair<EventType, String>, Int>()

    override suspend fun unfinishedSession() = unfinished

    override suspend fun recent(limit: Int): List<RecentEvent> = recentEvents.take(limit)

    override suspend fun countOf(type: EventType, subjectId: String): Int = counts[type to subjectId] ?: 0
}
