package com.paulchibamba.margin.domain.progress.rules

import com.paulchibamba.margin.domain.model.Concept
import com.paulchibamba.margin.domain.progress.ChapterProgress
import com.paulchibamba.margin.domain.progress.FactKey
import com.paulchibamba.margin.domain.progress.PastReward

internal object ChapterFacts {
    private const val TITLE_SEPARATOR = "; "

    fun of(chapter: ChapterProgress): Map<String, String> = buildMap {
        put(FactKey.BOOK, chapter.bookTitle)
        put(FactKey.CHAPTER, chapter.chapter.title)
        put(FactKey.INTRODUCED, chapter.introduced.size.toString())
        put(FactKey.TOTAL, chapter.total.toString())
        put(FactKey.REMAINING, chapter.remaining.size.toString())
        put(FactKey.CONCEPTS, chapter.introduced.joinToString(TITLE_SEPARATOR, transform = Concept::title))
        chapter.remaining.firstOrNull()?.let { next -> put(FactKey.NEXT, next.title) }
    }

    fun isAbout(reward: PastReward, chapter: ChapterProgress): Boolean =
        reward.facts[FactKey.BOOK] == chapter.bookTitle && reward.facts[FactKey.CHAPTER] == chapter.chapter.title
}
