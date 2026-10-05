package com.paulchibamba.margin.domain.usecase

import com.paulchibamba.margin.domain.feed.NoteExcerpt
import com.paulchibamba.margin.domain.model.Concept
import com.paulchibamba.margin.domain.progress.GeneratedPost
import com.paulchibamba.margin.domain.progress.RewardEligibility
import com.paulchibamba.margin.domain.progress.RewardHistory
import com.paulchibamba.margin.domain.progress.RewardPlanner
import com.paulchibamba.margin.domain.progress.RewardSeed
import com.paulchibamba.margin.domain.progress.TemplateWriter
import com.paulchibamba.margin.domain.progress.ValidationSources
import com.paulchibamba.margin.domain.repository.Clock
import com.paulchibamba.margin.domain.repository.ContentRepository
import com.paulchibamba.margin.domain.repository.GeneratedPostRepository
import java.time.Instant
import javax.inject.Inject
import kotlin.random.Random

class BakeTemplatePosts @Inject constructor(
    private val buildFacts: BuildProgressFacts,
    private val generatedPosts: GeneratedPostRepository,
    private val content: ContentRepository,
    private val clock: Clock,
    private val random: Random,
) {
    suspend operator fun invoke(): Int {
        val now = clock.now()
        val history = RewardHistory(generatedPosts.all().map(GeneratedPost::toPastReward))
        val seeds = RewardEligibility().seedsFrom(buildFacts(history), history)
        val plan = RewardPlanner(random).plan(seeds, history, now)
        val titles = content.concepts().map(Concept::title).toSet()
        return generatedPosts.insert(plan.rewards.mapNotNull { seed -> postFor(seed, titles, now) })
    }

    private suspend fun postFor(seed: RewardSeed, titles: Set<String>, now: Instant): GeneratedPost? {
        val noteText = seed.noteId?.let { note -> content.note(note) }?.let { note -> NoteExcerpt.plainText(note.html) }
        val draft = TemplateWriter(random).write(seed, ValidationSources(titles, noteText)) ?: return null
        return GeneratedPost.from(seed, draft, GeneratedPost.TEMPLATE_WRITER, now)
    }
}
