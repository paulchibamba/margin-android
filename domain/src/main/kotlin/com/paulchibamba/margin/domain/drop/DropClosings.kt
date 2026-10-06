package com.paulchibamba.margin.domain.drop

import com.paulchibamba.margin.domain.progress.GeneratedPost
import com.paulchibamba.margin.domain.progress.ProgressFacts
import com.paulchibamba.margin.domain.progress.RewardHistory
import com.paulchibamba.margin.domain.progress.RewardKind
import com.paulchibamba.margin.domain.progress.RewardSeed
import com.paulchibamba.margin.domain.progress.TemplateWriter
import com.paulchibamba.margin.domain.progress.ValidationSources
import com.paulchibamba.margin.domain.progress.rules.ChapterFacts
import com.paulchibamba.margin.domain.progress.rules.NowYouCanRule
import java.time.Instant
import kotlin.random.Random

class DropClosings(private val random: Random) {

    fun templatesFor(facts: ProgressFacts, history: RewardHistory, now: Instant): List<GeneratedPost> {
        val sources = ValidationSources(conceptTitles = facts.introduced.map { concept -> concept.title }.toSet())
        return (nowYouCanSeeds(facts, history) + listOfNotNull(zoomOutSeed(facts)))
            .mapNotNull { seed -> templatePostOf(seed, sources, now) }
    }

    private fun nowYouCanSeeds(facts: ProgressFacts, history: RewardHistory): List<RewardSeed> =
        NowYouCanRule().seedsFrom(facts, history)

    private fun zoomOutSeed(facts: ProgressFacts): RewardSeed? {
        val newest = facts.introductions.maxByOrNull { introduction -> introduction.on }?.concept
        val chapter = newest?.let(facts::chapterOf) ?: facts.chapters.firstOrNull { it.introduced.isNotEmpty() }
        chapter ?: return null
        return RewardSeed(RewardKind.ZoomOut, chapter.introduced.map { it.id }, facts = ChapterFacts.of(chapter))
    }

    private fun templatePostOf(seed: RewardSeed, sources: ValidationSources, now: Instant): GeneratedPost? =
        TemplateWriter(random).write(seed, sources)
            ?.let { draft -> GeneratedPost.from(seed, draft, GeneratedPost.TEMPLATE_WRITER, now) }
}
