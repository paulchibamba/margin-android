package com.paulchibamba.margin.domain.progress

import com.paulchibamba.margin.domain.model.Chapter

internal object ChapterProgressCalculator {

    fun of(context: ProgressContext, introductions: List<Introduction>): List<ChapterProgress> =
        context.sources.chapters.mapNotNull { chapter -> progressOf(chapter, context, introductions) }

    private fun progressOf(
        chapter: Chapter,
        context: ProgressContext,
        introductions: List<Introduction>,
    ): ChapterProgress? {
        val concepts = context.countedConcepts.filter { concept ->
            concept.bookSlug == chapter.bookSlug && concept.chapter == chapter.number
        }
        if (concepts.isEmpty()) return null
        val (introduced, remaining) = concepts.partition(context::isIntroduced)
        return ChapterProgress(
            chapter = chapter,
            bookTitle = context.bookTitleOf(chapter.bookSlug),
            introduced = introduced,
            remaining = remaining,
            remembered = concepts.filter(context::isRemembered),
            frontier = context.frontierOf(chapter.bookSlug),
            firstIntroducedOn = introductions.filter { it.concept in concepts }.minOfOrNull(Introduction::on),
        )
    }
}
