package com.paulchibamba.margin.domain.tracking

import com.paulchibamba.margin.domain.actions.PostAction
import com.paulchibamba.margin.domain.memory.Rating
import com.paulchibamba.margin.domain.model.PostId
import com.paulchibamba.margin.domain.repository.Clock
import java.time.Instant
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.time.Duration
import kotlin.time.toKotlinDuration

@Singleton
class PostAttention @Inject constructor(
    private val clock: Clock,
    private val events: EventRecorder,
) : SessionListener {
    private val lock = Any()
    private val tracker = AttentionTracker(clock)
    private var visit: PostVisit? = null
    private var impressionAt: Instant = clock.now()
    private var furthestIndex = -1
    private var hasReadSource = false
    private var isFeedShown = false
    private var isScrolling = false

    fun onFeedOpened() = synchronized(lock) {
        visit = null
        furthestIndex = -1
    }

    fun onSettled(next: PostVisit) {
        synchronized(lock) {
            val previous = visit
            if (previous?.pageIndex == next.pageIndex) return
            previous?.let { events.record(exposureOf(it, exitedSession = false)) }
            val isRevisit = next.pageIndex < furthestIndex
            if (previous != null && isRevisit) events.record(Event.PostRevisit(next.postId, previous.step, next.step))
            begin(next)
            events.record(impressionOf(next, isRevisit))
        }
    }

    fun onLeftPosts() = synchronized(lock) {
        visit?.let { events.record(exposureOf(it, exitedSession = false)) }
        visit = null
    }

    fun onInteraction(postId: PostId, kind: InteractionKind) = synchronized(lock) {
        if (kind == InteractionKind.READ_SOURCE) hasReadSource = true
        events.record(Event.PostInteraction(postId, kind, sinceImpression()))
    }

    fun onAction(postId: PostId, action: PostAction) {
        if (action == PostAction.READ) onInteraction(postId, InteractionKind.READ_SOURCE)
        events.record(Event.PostActionTaken(postId, action))
    }

    fun onAnswer(postId: PostId, isCorrect: Boolean?, timeToAnswer: Duration, grade: Rating) = synchronized(lock) {
        events.record(Event.PostAnswer(postId, isCorrect, timeToAnswer, grade, openedSourceFirst = hasReadSource))
    }

    fun onFeedShown(shown: Boolean) = synchronized(lock) {
        isFeedShown = shown
        tracker.setSettled(isFeedShown && !isScrolling)
    }

    fun onScrolling(scrolling: Boolean) = synchronized(lock) {
        isScrolling = scrolling
        tracker.setSettled(isFeedShown && !isScrolling)
    }

    fun onInput() = synchronized(lock) { tracker.onInput() }

    fun onForegroundChanged(foreground: Boolean) = synchronized(lock) { tracker.setForeground(foreground) }

    fun onInteractiveChanged(interactive: Boolean) = synchronized(lock) { tracker.setInteractive(interactive) }

    override fun eventsAtStart(): List<Event> = synchronized(lock) {
        val current = visit ?: return emptyList()
        restartClock()
        listOf(impressionOf(current, isRevisit = false))
    }

    override fun eventsAtEnd(): List<Event> = synchronized(lock) {
        val current = visit ?: return emptyList()
        listOf(exposureOf(current, exitedSession = true)).also { restartClock() }
    }

    private fun begin(next: PostVisit) {
        visit = next
        furthestIndex = maxOf(furthestIndex, next.pageIndex)
        hasReadSource = false
        restartClock()
    }

    private fun restartClock() {
        tracker.restart()
        impressionAt = clock.now()
    }

    private fun impressionOf(visit: PostVisit, isRevisit: Boolean) = Event.PostImpression(
        visit.postId, visit.conceptId, visit.format, visit.skinName, visit.source, visit.step, isRevisit,
    )

    private fun exposureOf(visit: PostVisit, exitedSession: Boolean): Event.PostExposure {
        val totals = tracker.totals()
        return Event.PostExposure(
            visit.postId, totals.active, totals.idle, visit.words, visit.expectedTime, exitedSession,
        )
    }

    private fun sinceImpression(): Duration = java.time.Duration.between(impressionAt, clock.now()).toKotlinDuration()
}
