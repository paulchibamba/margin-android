package com.paulchibamba.margin.domain.tracking

import com.paulchibamba.margin.domain.repository.EventLog

class FakeEventLog : EventLog {
    var unfinished: UnfinishedSession? = null
    var recentEvents: List<RecentEvent> = emptyList()

    override suspend fun unfinishedSession() = unfinished

    override suspend fun recent(limit: Int): List<RecentEvent> = recentEvents.take(limit)
}
