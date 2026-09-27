package com.paulchibamba.margin.domain.usecase

import com.paulchibamba.margin.domain.feed.FeedState
import com.paulchibamba.margin.domain.model.BookSlug
import com.paulchibamba.margin.domain.model.Chapter
import com.paulchibamba.margin.domain.model.ChapterRef
import com.paulchibamba.margin.domain.model.NoteOutline
import com.paulchibamba.margin.domain.progression.ReadingOnlyChapters
import com.paulchibamba.margin.domain.progression.ReadingState
import com.paulchibamba.margin.domain.repository.ContentRepository
import com.paulchibamba.margin.domain.repository.ProgressRepository
import com.paulchibamba.margin.domain.repository.SettingsRepository
import com.paulchibamba.margin.domain.rewards.BookCompletion
import com.paulchibamba.margin.domain.rewards.BookCompletionCalculator
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import javax.inject.Inject

class ObserveBook @Inject constructor(
    private val content: ContentRepository,
    private val progress: ProgressRepository,
    private val settings: SettingsRepository,
) {
    operator fun invoke(book: BookSlug): Flow<BookChapters> = combine(
        progress.observeReading(),
        settings.observeReadingOnlyChapters(),
        progress.observeFeedState(),
    ) { reading, readingOnly, feedState ->
        chaptersOf(book, reading, readingOnly, completionOf(book, readingOnly, feedState))
    }.filterNotNull()

    private suspend fun completionOf(book: BookSlug, readingOnly: ReadingOnlyChapters, feedState: FeedState?) =
        BookCompletionCalculator(readingOnly)
            .completionOf(book, content.concepts(), feedState?.conceptProgress.orEmpty())

    private suspend fun chaptersOf(
        book: BookSlug,
        reading: ReadingState,
        readingOnly: ReadingOnlyChapters,
        completion: BookCompletion,
    ): BookChapters? {
        val bookInfo = content.books().firstOrNull { it.slug == book } ?: return null
        val notesByChapter = content.noteOutlines()
            .filter { it.bookSlug == book }
            .sortedBy(NoteOutline::position)
            .groupBy { it.position.chapter }
        val chapters = content.chapters().filter { it.bookSlug == book }.sortedBy(Chapter::number).map { chapter ->
            chapterReading(chapter, notesByChapter[chapter.number].orEmpty(), reading, readingOnly)
        }
        return BookChapters(bookInfo, chapters, completion)
    }

    private fun chapterReading(
        chapter: Chapter,
        notes: List<NoteOutline>,
        reading: ReadingState,
        readingOnly: ReadingOnlyChapters,
    ) = ChapterReading(
        chapter = chapter.copy(isReadingOnly = readingOnly.contains(chapter.bookSlug, chapter.number)),
        tally = NoteTally.of(notes, reading.readNotes),
        isKnown = reading.isKnown(ChapterRef(chapter.bookSlug, chapter.number)),
        nextNote = notes.firstOrNull { it.id !in reading.readNotes }?.id,
    )
}
