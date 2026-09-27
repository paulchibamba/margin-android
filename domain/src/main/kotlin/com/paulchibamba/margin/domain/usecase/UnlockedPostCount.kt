package com.paulchibamba.margin.domain.usecase

import com.paulchibamba.margin.domain.feed.FeedState
import com.paulchibamba.margin.domain.model.Concept
import com.paulchibamba.margin.domain.model.NoteOutline
import com.paulchibamba.margin.domain.progression.ReadingOnlyChapters
import com.paulchibamba.margin.domain.repository.ContentRepository
import com.paulchibamba.margin.domain.repository.SettingsRepository
import javax.inject.Inject

class UnlockedPostCount @Inject constructor(
    private val content: ContentRepository,
    private val settings: SettingsRepository,
) {
    suspend fun at(outline: NoteOutline, state: FeedState?): Int {
        val readingOnly = settings.readingOnlyChapters()
        val unlocked = content.concepts()
            .filter { concept -> isUnlockedBy(concept, outline, readingOnly) && state?.isIntroduced(concept) != true }
            .map { it.id }
            .toSet()
        return content.posts().count { it.conceptId in unlocked }
    }

    private fun isUnlockedBy(concept: Concept, outline: NoteOutline, readingOnly: ReadingOnlyChapters): Boolean =
        concept.bookSlug == outline.bookSlug && concept.position == outline.position && concept !in readingOnly
}
