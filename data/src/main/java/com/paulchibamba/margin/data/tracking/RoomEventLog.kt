package com.paulchibamba.margin.data.tracking

import com.paulchibamba.margin.data.database.MarginDatabase
import com.paulchibamba.margin.domain.repository.EventLog
import com.paulchibamba.margin.domain.tracking.Event
import com.paulchibamba.margin.domain.tracking.EventType
import com.paulchibamba.margin.domain.tracking.LoggedEvent
import com.paulchibamba.margin.domain.tracking.RecentEvent
import com.paulchibamba.margin.domain.tracking.SessionId
import com.paulchibamba.margin.domain.tracking.SessionStarted
import com.paulchibamba.margin.domain.tracking.UnfinishedSession
import java.time.Instant
import javax.inject.Inject

class RoomEventLog @Inject constructor(private val database: MarginDatabase) : EventLog {
    private val eventDao get() = database.eventDao()

    override suspend fun unfinishedSession(): UnfinishedSession? {
        val start = eventDao.latestSessionStart() ?: return null
        val sessionId = start.sessionId ?: return null
        if (eventDao.sessionEndCount(sessionId) > 0) return null
        val lastEventAt = eventDao.lastEventAt(sessionId) ?: start.at
        return UnfinishedSession(
            id = SessionId(sessionId),
            startedAt = Instant.ofEpochMilli(start.at),
            lastEventAt = Instant.ofEpochMilli(lastEventAt),
        )
    }

    override suspend fun recent(limit: Int): List<RecentEvent> = eventDao.latest(limit).map(EventMapper::toRecent)

    override suspend fun countOf(type: EventType, subjectId: String): Int = eventDao.countOf(type.key, subjectId)

    override suspend fun between(from: Instant, until: Instant): List<LoggedEvent> =
        eventDao.between(from.toEpochMilli(), until.toEpochMilli()).mapNotNull(EventMapper::toLogged)

    override suspend fun sessionStarts(from: Instant, until: Instant): List<SessionStarted> =
        eventDao.ofTypeBetween(EventType.SESSION_START.key, from.toEpochMilli(), until.toEpochMilli())
            .mapNotNull(EventMapper::toLogged)
            .mapNotNull(::sessionStartedOf)

    override suspend fun firstEventAt(): Instant? = eventDao.firstAt()?.let(Instant::ofEpochMilli)

    override suspend fun deleteBefore(cutoff: Instant): Int = eventDao.deleteBefore(cutoff.toEpochMilli())

    private fun sessionStartedOf(logged: LoggedEvent): SessionStarted? =
        (logged.event as? Event.SessionStart)?.let { start -> SessionStarted(logged.at, start.entry) }
}
