package com.paulchibamba.margin.domain.tracking

import com.paulchibamba.margin.domain.repository.EventLog
import java.time.Instant

class FakeEventLog : EventLog {
    var unfinished: UnfinishedSession? = null
    var recentEvents: List<RecentEvent> = emptyList()
    val counts = mutableMapOf<Pair<EventType, String>, Int>()
    val logged = mutableListOf<LoggedEvent>()

    override suspend fun unfinishedSession() = unfinished

    override suspend fun recent(limit: Int): List<RecentEvent> = recentEvents.take(limit)

    override suspend fun countOf(type: EventType, subjectId: String): Int = counts[type to subjectId] ?: 0

    override suspend fun between(from: Instant, until: Instant): List<LoggedEvent> =
        logged.filter { !it.at.isBefore(from) && it.at.isBefore(until) }

    override suspend fun firstEventAt(): Instant? = logged.minOfOrNull(LoggedEvent::at)

    override suspend fun deleteBefore(cutoff: Instant): Int {
        val old = logged.filter { it.at.isBefore(cutoff) }
        logged.removeAll(old)
        return old.size
    }
}
