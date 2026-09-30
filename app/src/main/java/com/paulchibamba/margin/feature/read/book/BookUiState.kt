package com.paulchibamba.margin.feature.read.book

import com.paulchibamba.margin.domain.model.BookCover
import com.paulchibamba.margin.domain.model.ChapterRef
import com.paulchibamba.margin.domain.usecase.BookChapters
import com.paulchibamba.margin.domain.usecase.ChapterReading
import com.paulchibamba.margin.feature.feed.shortChapterTitle

data class BookUiState(
    val title: String = "",
    val introduced: Int = 0,
    val remembered: Int = 0,
    val conceptCount: Int = 0,
    val chapters: List<ChapterRowState> = emptyList(),
    val undoChapter: ChapterRef? = null,
    val coverPath: String? = null,
    val isLoading: Boolean = true,
) {
    val hasCover: Boolean
        get() = coverPath != null

    companion object {
        fun of(book: BookChapters, undoChapter: ChapterRef?, cover: BookCover? = null): BookUiState {
            val current = book.chapters.indexOfFirst { !isFinished(it) }
            return BookUiState(
                title = book.book.title,
                introduced = book.completion.introduced,
                remembered = book.completion.remembered,
                conceptCount = book.completion.total,
                chapters = book.chapters.mapIndexed { index, chapter -> rowOf(chapter, index, current) },
                undoChapter = undoChapter,
                coverPath = cover?.imagePath,
                isLoading = false,
            )
        }

        private fun isFinished(chapter: ChapterReading): Boolean = chapter.isDone || chapter.tally.noteCount == 0

        private fun rowOf(chapter: ChapterReading, index: Int, current: Int) = ChapterRowState(
            chapter = ChapterRef(chapter.chapter.bookSlug, chapter.chapter.number),
            title = shortChapterTitle(chapter.chapter.title).ifEmpty { chapter.chapter.title },
            tally = chapter.tally,
            isReadingOnly = chapter.chapter.isReadingOnly,
            isKnown = chapter.isKnown,
            isDone = chapter.isDone,
            isCurrent = index == current,
            isAhead = current in 0 until index && !isFinished(chapter),
            nextNote = chapter.nextNote,
        )
    }
}
