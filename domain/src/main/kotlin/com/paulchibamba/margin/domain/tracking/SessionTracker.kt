package com.paulchibamba.margin.domain.tracking

import com.paulchibamba.margin.domain.repository.Clock
import com.paulchibamba.margin.domain.repository.EventLog
import com.paulchibamba.margin.domain.repository.EventSink
import com.paulchibamba.margin.domain.repository.SessionTally
import java.time.Instant
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds
import kotlin.time.toKotlinDuration
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

@Singleton
class SessionTracker @Inject constructor(
    private val clock: Clock,
    private val sink: EventSink,
    private val tally: SessionTally,
    private val log: EventLog,
    private val sessionIds: SessionIdFactory,
) : EventRecorder {
    private val mutex = Mutex()

    @Volatile
    private var openSession: OpenSession? = null
    private var isForeground = false
    private var away: Away? = null
    private var lastInputAt: Instant = Instant.EPOCH
    private var hasClosedUnfinished = false

    override fun record(event: Event) {
        sink.append(LoggedEvent(clock.now(), openSession?.id, event))
    }

    suspend fun onForeground(entry: SessionEntry) = mutex.withLock {
        closeUnfinishedOnce()
        isForeground = true
        lastInputAt = clock.now()
        val leftAt = away.also { away = null }
        val session = openSession
        if (session != null) {
            if (leftAt == null || isWithinGrace(leftAt.since)) return@withLock
            end(session, leftAt.since, leftAt.reason)
        }
        start(entry)
    }

    suspend fun onBackground(isScreenOn: Boolean) = mutex.withLock {
        isForeground = false
        val reason = if (isScreenOn) SessionEndReason.BACKGROUND else SessionEndReason.SCREEN_OFF
        away = Away(clock.now(), reason)
        sink.flush()
    }

    suspend fun endIfAway() = mutex.withLock {
        val leftAt = away ?: return@withLock
        val session = openSession ?: return@withLock
        if (isForeground || isWithinGrace(leftAt.since)) return@withLock
        away = null
        end(session, leftAt.since, leftAt.reason)
    }

    suspend fun endIfIdle() = mutex.withLock {
        val session = openSession ?: return@withLock
        if (!isForeground || elapsedSince(lastInputAt) < IDLE_LIMIT) return@withLock
        end(session, clock.now(), SessionEndReason.IDLE)
    }

    suspend fun onInput() = mutex.withLock {
        lastInputAt = clock.now()
        if (isForeground && openSession == null) start(SessionEntry.RESUME)
    }

    private suspend fun closeUnfinishedOnce() {
        if (hasClosedUnfinished) return
        hasClosedUnfinished = true
        val unfinished = log.unfinishedSession() ?: return
        val ending = sessionEnd(unfinished.startedAt, unfinished.lastEventAt, SessionEndReason.UNKNOWN)
        sink.append(LoggedEvent(unfinished.lastEventAt, unfinished.id, ending))
    }

    private fun start(entry: SessionEntry) {
        val session = OpenSession(sessionIds.newSessionId(), clock.now())
        openSession = session
        sink.append(LoggedEvent(session.startedAt, session.id, Event.SessionStart(entry)))
    }

    private suspend fun end(session: OpenSession, at: Instant, reason: SessionEndReason) {
        openSession = null
        sink.append(LoggedEvent(at, session.id, sessionEnd(session.startedAt, at, reason)))
        sink.flush()
    }

    private suspend fun sessionEnd(startedAt: Instant, endedAt: Instant, reason: SessionEndReason): Event.SessionEnd {
        val counts = tally.countBetween(startedAt, endedAt)
        val duration = java.time.Duration.between(startedAt, endedAt).toKotlinDuration()
        return Event.SessionEnd(reason, duration, counts.posts, counts.notes)
    }

    private fun isWithinGrace(leftAt: Instant) = elapsedSince(leftAt) < AWAY_GRACE

    private fun elapsedSince(instant: Instant) = java.time.Duration.between(instant, clock.now()).toKotlinDuration()

    private data class OpenSession(val id: SessionId, val startedAt: Instant)

    private data class Away(val since: Instant, val reason: SessionEndReason)

    companion object {
        val AWAY_GRACE = 30.seconds
        val IDLE_LIMIT = 5.minutes
    }
}
