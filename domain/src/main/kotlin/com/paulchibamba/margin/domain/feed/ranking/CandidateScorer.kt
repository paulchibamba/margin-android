package com.paulchibamba.margin.domain.feed.ranking

import com.paulchibamba.margin.domain.feed.Candidate
import com.paulchibamba.margin.domain.feed.CandidateSource
import com.paulchibamba.margin.domain.feed.Confidence
import com.paulchibamba.margin.domain.feed.FeedConfig
import com.paulchibamba.margin.domain.feed.FeedState
import com.paulchibamba.margin.domain.feed.LearningLibrary
import com.paulchibamba.margin.domain.feed.RecallEstimate
import com.paulchibamba.margin.domain.model.BookSlug
import com.paulchibamba.margin.domain.progression.PriorityShare
import java.time.Instant
import kotlin.math.max
import kotlin.math.min
import kotlin.random.Random

class CandidateScorer(
    private val config: FeedConfig,
    private val recallEstimate: RecallEstimate,
    private val random: Random,
    private val priorityShare: PriorityShare = PriorityShare(),
) {

    fun scoreAll(
        candidates: List<Candidate>,
        library: LearningLibrary,
        state: FeedState,
        now: Instant,
    ): List<ScoredCandidate> {
        val context = Context(library, state, now)
        return candidates.map { candidate -> ScoredCandidate(candidate, context.score(candidate)) }
    }

    private inner class Context(val library: LearningLibrary, val state: FeedState, val now: Instant) {
        private val recentFormats = state.history.takeLast(config.noveltyWindow).map { it.format }.toSet()
        private val introducedCounts = library.conceptsInBookOrder
            .filter(state::isIntroduced)
            .groupingBy { it.bookSlug }
            .eachCount()

        fun score(candidate: Candidate) = ScoreBreakdown(
            buildMap {
                put(ScorePart.SOURCE, config.sourceWeight.getValue(candidate.source))
                put(ScorePart.FORMAT, config.formatWeight * state.affinity.valueOf(candidate.post.format))
                put(ScorePart.NOVELTY, novelty(candidate))
                put(ScorePart.JITTER, random.nextDouble() * config.jitterMax)
                putAll(sourceSpecificParts(candidate))
            },
        )

        private fun novelty(candidate: Candidate): Double =
            if (candidate.post.format in recentFormats) 0.0 else config.noveltyWeight

        private fun sourceSpecificParts(candidate: Candidate): Map<ScorePart, Double> = when (candidate.source) {
            CandidateSource.REVIEW -> reviewParts(candidate)
            CandidateSource.ANGLE -> angleParts(candidate)
            CandidateSource.NEW -> newConceptParts(candidate.post.bookSlug)
            else -> emptyMap()
        }

        private fun reviewParts(candidate: Candidate): Map<ScorePart, Double> = buildMap {
            put(ScorePart.URGENCY, urgency(candidate))
            if (confidenceOf(candidate) == Confidence.GOT) put(ScorePart.PROVE_IT, config.proveItWeight)
        }

        private fun angleParts(candidate: Candidate): Map<ScorePart, Double> =
            if (confidenceOf(candidate) == Confidence.LOST) {
                mapOf(ScorePart.RETEACH to config.reteachWeight)
            } else {
                emptyMap()
            }

        private fun newConceptParts(book: BookSlug): Map<ScorePart, Double> =
            mapOf(ScorePart.PRIORITY to priority(book), ScorePart.BOOK_WAIT to bookWait(book))

        private fun confidenceOf(candidate: Candidate): Confidence? =
            state.progressOf(candidate.post.conceptId).confidence

        private fun urgency(candidate: Candidate): Double {
            val recall = recallEstimate.of(state.progressOf(candidate.post.conceptId), now)
            return min(config.urgencyCap, config.urgencyWeight * max(0.0, config.desiredRetention - recall))
        }

        private fun priority(book: BookSlug): Double {
            val target = priorityShare.targetShare(book, library.bookSettings)
            val actual = priorityShare.actualShare(book, introducedCounts, library.bookSettings)
            return (config.priorityWeight * (target - actual)).coerceIn(-config.priorityCap, config.priorityCap)
        }

        private fun bookWait(book: BookSlug): Double {
            val postsSinceLastNew = state.step - (state.bookLastNewStep[book] ?: 0)
            return min(config.bookWaitCap, config.bookWaitWeight * postsSinceLastNew)
        }
    }
}
