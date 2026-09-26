package com.paulchibamba.margin.domain.usecase

import com.paulchibamba.margin.domain.feed.LearningLibrary
import com.paulchibamba.margin.domain.model.Concept
import com.paulchibamba.margin.domain.repository.ContentRepository
import com.paulchibamba.margin.domain.repository.ProgressRepository
import com.paulchibamba.margin.domain.repository.SettingsRepository
import javax.inject.Inject

class LibraryLoader @Inject constructor(
    private val content: ContentRepository,
    private val progress: ProgressRepository,
    private val settings: SettingsRepository,
) {
    suspend fun load(): LearningLibrary {
        val concepts = content.concepts()
        return LearningLibrary(
            books = content.books(),
            concepts = concepts,
            posts = content.posts(),
            sourceNotes = content.notes(concepts.mapNotNull(Concept::sourceNoteId)).associateBy { it.id },
            bookSettings = settings.bookSettings(),
            readingOnlyChapters = settings.readingOnlyChapters(),
            readingProgress = progress.reading().progressWith(content.noteOutlines()),
        )
    }
}
