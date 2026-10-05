package com.paulchibamba.margin.domain.actions

import com.paulchibamba.margin.domain.feed.CardGrader
import com.paulchibamba.margin.domain.feed.ConceptProgress
import com.paulchibamba.margin.domain.feed.Confidence
import com.paulchibamba.margin.domain.feed.FeedState
import com.paulchibamba.margin.domain.memory.CardState
import com.paulchibamba.margin.domain.memory.FsrsScheduler
import com.paulchibamba.margin.domain.memory.MemoryCard
import com.paulchibamba.margin.domain.memory.Rating
import com.paulchibamba.margin.domain.model.Post
import java.time.Instant

class PostActionHandler(scheduler: FsrsScheduler) {

    private val grader = CardGrader(scheduler)

    fun apply(state: FeedState, post: Post, action: PostAction, now: Instant): ActionOutcome {
        val logEntry = ActionLogEntry(now, state.step, post.id, post.conceptId, action)
        return when (action) {
            PostAction.GOT -> ActionOutcome(markGot(state, post), Nudge.TEST_COMING_SOON, logEntry)
            PostAction.LOST -> markLost(state, post, now, logEntry)
            PostAction.READ -> ActionOutcome(state, nudge = null, logEntry)
            PostAction.SAVE -> ActionOutcome(markSaved(state, post), nudge = null, logEntry)
            PostAction.LESS -> ActionOutcome(markLess(state, post), nudge = null, logEntry)
        }
    }

    private fun markSaved(state: FeedState, post: Post): FeedState = state.copy(savedPosts = state.savedPosts + post.id)

    private fun markLess(state: FeedState, post: Post): FeedState =
        state.copy(affinity = state.affinity.afterLess(post.affinityKey))

    private fun markGot(state: FeedState, post: Post): FeedState =
        state.withProgress(post.conceptId, state.progressOf(post.conceptId).copy(confidence = Confidence.GOT))

    private fun markLost(state: FeedState, post: Post, now: Instant, logEntry: ActionLogEntry): ActionOutcome {
        val lost = state.progressOf(post.conceptId).copy(confidence = Confidence.LOST, lostAtStep = state.step)
        val lapse = uncountedLapseCard(lost)?.let { card -> grader.grade(card, post, Rating.AGAIN, now, dwell = null) }
        val progress = lapse?.let { graded -> lost.copy(card = graded.card, isLostGraded = true) } ?: lost
        return ActionOutcome(
            state = state.withProgress(post.conceptId, progress),
            nudge = Nudge.ANOTHER_ANGLE_COMING,
            logEntry = logEntry,
            reviewLogEntry = lapse?.logEntry,
        )
    }

    private fun uncountedLapseCard(progress: ConceptProgress): MemoryCard? =
        progress.card?.takeIf { card -> card.state != CardState.NEW && !progress.isLostGraded }
}
