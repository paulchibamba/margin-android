package com.paulchibamba.margin.domain.tracking

import com.paulchibamba.margin.domain.model.NoteId
import com.paulchibamba.margin.domain.repository.Clock
import com.paulchibamba.margin.domain.repository.EventLog
import com.paulchibamba.margin.domain.repository.EventSink
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.time.Duration

@Singleton
class NoteAttention @Inject constructor(
    clock: Clock,
    private val events: EventRecorder,
    private val sink: EventSink,
    private val log: EventLog,
) : PresenceListener {
    private val lock = Any()
    private val tracker = AttentionTracker(clock)
    private var visit: NoteVisit? = null
    private var scroll = ScrollBackDetector()

    suspend fun onOpened(next: NoteVisit) {
        val openCount = previousOpensOf(next.noteId) + 1
        synchronized(lock) {
            visit = next
            scroll = ScrollBackDetector()
            tracker.restart()
            events.record(Event.NoteOpen(next.noteId, next.via, openCount))
        }
    }

    fun onClosed(isMarkedRead: Boolean) {
        synchronized(lock) {
            val current = visit ?: return
            events.record(exposureOf(current, isMarkedRead))
            visit = null
        }
    }

    fun onScrolled(position: ScrollPosition) = synchronized(lock) { scroll.onScroll(position) }

    fun onZoomedIn() {
        synchronized(lock) {
            val current = visit?.takeIf(NoteVisit::hasImages) ?: return
            events.record(Event.ImageZoom(postId = null, noteId = current.noteId))
        }
    }

    fun onShown(shown: Boolean) = synchronized(lock) { tracker.setSettled(shown) }

    override fun onInput() = synchronized(lock) { tracker.onInput() }

    override fun onForegroundChanged(foreground: Boolean) = synchronized(lock) { tracker.setForeground(foreground) }

    override fun onInteractiveChanged(interactive: Boolean) = synchronized(lock) { tracker.setInteractive(interactive) }

    private suspend fun previousOpensOf(note: NoteId): Int {
        sink.flush()
        return log.countOf(EventType.NOTE_OPEN, note.value)
    }

    private fun exposureOf(visit: NoteVisit, isMarkedRead: Boolean): Event.NoteExposure {
        val totals = tracker.totals()
        return Event.NoteExposure(
            noteId = visit.noteId,
            activeTime = totals.active,
            idleTime = totals.idle,
            words = visit.words,
            wordsPerMinute = wordsPerMinute(visit.words, totals.active),
            maxScrollPercent = scroll.maxScrollPercent,
            scrollBacks = scroll.scrollBacks,
            isMarkedRead = isMarkedRead,
        )
    }

    private fun wordsPerMinute(words: Int, active: Duration): Int =
        if (active.inWholeSeconds == 0L) 0 else (words * SECONDS_PER_MINUTE / active.inWholeSeconds).toInt()

    private companion object {
        const val SECONDS_PER_MINUTE = 60L
    }
}
