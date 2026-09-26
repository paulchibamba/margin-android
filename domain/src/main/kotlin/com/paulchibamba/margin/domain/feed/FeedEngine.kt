package com.paulchibamba.margin.domain.feed

import com.paulchibamba.margin.domain.feed.filter.FilterChain
import com.paulchibamba.margin.domain.feed.ranking.CandidatePicker
import com.paulchibamba.margin.domain.feed.ranking.CandidateScorer
import com.paulchibamba.margin.domain.feed.ranking.Pick
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
    private val delightSchedule = DelightSchedule(config.delightEvery, random)
    private val collector = CandidateCollector.from(config)
    private val filterChain = FilterChain.from(config)
    private val scorer = CandidateScorer(config, recallEstimate, random)
    private val picker = CandidatePicker(config.epsilon, random)
    private val recorder = FeedStepRecorder(delightSchedule)

    fun startingState(): FeedState = FeedState(delightAtStep = delightSchedule.nextAfter(step = 0))

    fun next(library: LearningLibrary, state: FeedState, now: Instant): FeedResult {
        val candidates = collector.collect(library, state, now)
        if (candidates.isEmpty()) return FeedResult.CaughtUp
        val filtered = filterChain.apply(candidates, state)
        val ranked = scorer.scoreAll(filtered.pool, library, state, now).sortedByDescending { it.score.total }
        val pick = picker.pick(ranked)
        val nextState = recorder.record(state, pick.chosen.candidate, now)
        return FeedResult.Next(itemFor(pick, ranked.size, filtered.appliedFilters, nextState, now), nextState)
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
