package com.paulchibamba.margin.domain.feed

import com.paulchibamba.margin.domain.memory.MemoryCard
import com.paulchibamba.margin.domain.model.Post
import com.paulchibamba.margin.domain.model.PostRole
import java.time.Instant

class FeedStepRecorder(private val delightSchedule: DelightSchedule) {

    fun record(state: FeedState, candidate: Candidate, now: Instant): FeedState {
        val step = state.step + 1
        val post = candidate.post
        val isIntroduction = introducesConcept(state, candidate)
        val progress = progressAfterShowing(state.progressOf(post.conceptId), post, step, isIntroduction, now)
        return state.copy(
            step = step,
            conceptProgress = state.conceptProgress + (post.conceptId to progress),
            seenPosts = state.seenPosts + (post.id to step),
            history = state.history + historyEntry(candidate, step),
            bookLastNewStep = if (isIntroduction) state.bookLastNewStep + (post.bookSlug to step)
            else state.bookLastNewStep,
            delightAtStep = delightAtAfter(state, candidate, step),
            lastPreviewAtStep = if (candidate.source == CandidateSource.PREVIEW) step else state.lastPreviewAtStep,
        )
    }

    private fun introducesConcept(state: FeedState, candidate: Candidate): Boolean =
        candidate.source == CandidateSource.NEW && !state.progressOf(candidate.post.conceptId).isIntroduced

    private fun progressAfterShowing(
        progress: ConceptProgress,
        post: Post,
        step: Int,
        isIntroduction: Boolean,
        now: Instant,
    ): ConceptProgress {
        val introduced = if (isIntroduction) introduce(progress, step, now) else progress
        val retaught = if (isReteach(introduced, post)) introduced.copy(retaughtAtStep = step) else introduced
        return retaught.copy(lastShownStep = step)
    }

    private fun introduce(progress: ConceptProgress, step: Int, now: Instant): ConceptProgress =
        progress.copy(introducedAtStep = step, card = MemoryCard.new(now))

    private fun isReteach(progress: ConceptProgress, post: Post): Boolean =
        progress.confidence == Confidence.LOST && post.role == PostRole.TEACH

    private fun historyEntry(candidate: Candidate, step: Int): FeedHistoryEntry {
        val post = candidate.post
        return FeedHistoryEntry(step, post.id, post.conceptId, post.format, post.role, candidate.source)
    }

    private fun delightAtAfter(state: FeedState, candidate: Candidate, step: Int): Int =
        if (candidate.source == CandidateSource.DELIGHT) delightSchedule.nextAfter(step) else state.delightAtStep
}
