package com.paulchibamba.margin.domain.progress.rules

import com.paulchibamba.margin.domain.progress.ChapterProgress
import com.paulchibamba.margin.domain.progress.FactKey
import com.paulchibamba.margin.domain.progress.PastReward
import com.paulchibamba.margin.domain.progress.ProgressFacts
import com.paulchibamba.margin.domain.progress.RewardHistory
import com.paulchibamba.margin.domain.progress.RewardKind
import com.paulchibamba.margin.domain.progress.RewardRule
import com.paulchibamba.margin.domain.progress.RewardSeed
import java.time.LocalDate
import java.time.temporal.ChronoUnit

class MilestoneRule : RewardRule {

    override fun seedsFrom(facts: ProgressFacts, history: RewardHistory): List<RewardSeed> =
        (chapterMilestonesOf(facts.chapters) + bookMilestonesOf(facts.chapters))
            .filterNot { milestone -> history.hasAny(RewardKind.Milestone) { past -> isSame(past, milestone) } }
            .map { milestone -> seedOf(milestone, facts.today) }

    private fun chapterMilestonesOf(chapters: List<ChapterProgress>): List<Milestone> = chapters.flatMap { chapter ->
        reachedKindsOf(chapter.isFullyIntroduced, chapter.isFullyRemembered).map { kind ->
            milestoneOf(SCOPE_CHAPTER, ChapterName.of(chapter.chapter.title), chapter.bookTitle, kind, listOf(chapter))
        }
    }

    private fun bookMilestonesOf(chapters: List<ChapterProgress>): List<Milestone> =
        chapters.groupBy(ChapterProgress::bookTitle).flatMap { (book, bookChapters) ->
            val kinds = reachedKindsOf(
                isFullyIntroduced = bookChapters.all(ChapterProgress::isFullyIntroduced),
                isFullyRemembered = bookChapters.all(ChapterProgress::isFullyRemembered),
            )
            kinds.map { kind -> milestoneOf(SCOPE_BOOK, book, book, kind, bookChapters) }
        }

    private fun reachedKindsOf(isFullyIntroduced: Boolean, isFullyRemembered: Boolean): List<String> =
        listOfNotNull(KIND_INTRODUCED.takeIf { isFullyIntroduced }, KIND_REMEMBERED.takeIf { isFullyRemembered })

    private fun milestoneOf(scope: String, name: String, book: String, kind: String, chapters: List<ChapterProgress>) =
        Milestone(
            scope = scope,
            name = name,
            book = book,
            kind = kind,
            introduced = chapters.sumOf { chapter -> chapter.introduced.size },
            remembered = chapters.sumOf { chapter -> chapter.remembered.size },
            conceptIds = chapters.flatMap { chapter -> chapter.introduced.map { concept -> concept.id } },
            firstIntroducedOn = chapters.mapNotNull(ChapterProgress::firstIntroducedOn).minOrNull(),
        )

    private fun isSame(past: PastReward, milestone: Milestone): Boolean =
        past.facts[FactKey.SCOPE] == milestone.scope && past.facts[FactKey.NAME] == milestone.name &&
            past.facts[FactKey.BOOK] == milestone.book && past.facts[FactKey.MILESTONE] == milestone.kind

    private fun seedOf(milestone: Milestone, today: LocalDate) = RewardSeed(
        kind = RewardKind.Milestone,
        conceptIds = milestone.conceptIds,
        facts = buildMap {
            put(FactKey.SCOPE, milestone.scope)
            put(FactKey.NAME, milestone.name)
            put(FactKey.BOOK, milestone.book)
            put(FactKey.MILESTONE, milestone.kind)
            put(FactKey.INTRODUCED, milestone.introduced.toString())
            put(FactKey.REMEMBERED, milestone.remembered.toString())
            milestone.firstIntroducedOn?.let { first -> put(FactKey.DAYS, daysSince(first, today).toString()) }
        },
    )

    private fun daysSince(first: LocalDate, today: LocalDate): Long = ChronoUnit.DAYS.between(first, today) + 1

    companion object {
        const val SCOPE_CHAPTER = "chapter"
        const val SCOPE_BOOK = "book"
        const val KIND_INTRODUCED = "introduced"
        const val KIND_REMEMBERED = "remembered"
    }
}
