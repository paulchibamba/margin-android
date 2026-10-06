package com.paulchibamba.margin.domain.feed

import com.paulchibamba.margin.domain.feed.filter.FilterChain
import com.paulchibamba.margin.domain.feed.ranking.CandidatePicker
import com.paulchibamba.margin.domain.feed.ranking.CandidateScorer
import com.paulchibamba.margin.domain.feed.ranking.Pick
import com.paulchibamba.margin.domain.feed.ranking.ScoreBreakdown
import com.paulchibamba.margin.domain.feed.ranking.ScoredCandidate
import com.paulchibamba.margin.domain.memory.FsrsScheduler
import com.paulchibamba.margin.domain.model.ConceptId
import java.time.Instant
import kotlin.random.Random
import kotlin.time.toKotlinDuration
import java.time.Duration as JavaDuration

class FeedEngine(
    private val config: FeedConfig,
    private val scheduler: FsrsScheduler,
    random: Random,
) {
    private val recallEstimate = RecallEstimate(scheduler, config)
    private val rewardSchedule = RewardSchedule(config.rewardEvery, random)
    private val collector = CandidateCollector.from(config)
    private val filterChain = FilterChain.from(config)
    private val scorer = CandidateScorer(config, recallEstimate, random)
    private val picker = CandidatePicker(config.epsilon, random)
    private val recorder = FeedStepRecorder(rewardSchedule)

    fun startingState(): FeedState = FeedState(rewardAtStep = rewardSchedule.nextAfter(step = 0))

    fun next(library: LearningLibrary, state: FeedState, now: Instant): FeedResult {
        val candidates = collector.collect(library, state, now)
        if (candidates.isEmpty()) return FeedResult.CaughtUp
        val filtered = filterChain.apply(candidates, state)
        val ranked = scorer.scoreAll(filtered.pool, library, state, now).sortedByDescending { it.score.total }
        val pick = picker.pick(ranked)
        val nextState = recorder.record(state, pick.chosen.candidate, now)
        return FeedResult.Next(itemFor(pick, ranked.size, filtered.appliedFilters, nextState, now), nextState)
    }

    fun show(state: FeedState, candidate: Candidate, now: Instant): FeedResult.Next {
        val nextState = recorder.record(state, candidate, now)
        return FeedResult.Next(preview(nextState, candidate, now), nextState)
    }

    fun preview(state: FeedState, candidate: Candidate, now: Instant): FeedItem {
        val unranked = Pick(ScoredCandidate(candidate, ScoreBreakdown(emptyMap())), rank = 1, wasExploration = false)
        return itemFor(unranked, poolSize = 1, appliedFilters = emptyList(), state, now)
    }

    private fun itemFor(
        pick: Pick,
        poolSize: Int,
        appliedFilters: List<String>,
        state: FeedState,
        now: Instant,
    ) = FeedItem(
        post = pick.chosen.candidate.post,
        source = pick.chosen.candidate.source,
        score = pick.chosen.score,
        rank = pick.rank,
        poolSize = poolSize,
        wasExploration = pick.wasExploration,
        appliedFilters = appliedFilters,
        memory = memoryOf(pick.chosen.candidate.post.conceptId, state, now),
        step = state.step,
    )

    private fun memoryOf(concept: ConceptId, state: FeedState, now: Instant): MemorySnapshot? {
        val progress = state.progressOf(concept)
        val card = progress.card ?: return null
        return MemorySnapshot(
            state = card.state,
            stability = card.stability,
            difficulty = card.difficulty,
            recall = recallEstimate.of(progress, now),
            dueIn = JavaDuration.between(now, card.due).toKotlinDuration(),
            reps = card.reps,
            lapses = card.lapses,
        )
    }
}
