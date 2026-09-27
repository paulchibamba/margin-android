package com.paulchibamba.margin.feature.read.book

import com.paulchibamba.margin.domain.model.Book
import com.paulchibamba.margin.domain.model.BookSlug
import com.paulchibamba.margin.domain.model.Chapter
import com.paulchibamba.margin.domain.model.NoteId
import com.paulchibamba.margin.domain.rewards.BookCompletion
import com.paulchibamba.margin.domain.usecase.BookChapters
import com.paulchibamba.margin.domain.usecase.ChapterReading
import com.paulchibamba.margin.domain.usecase.NoteTally
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes

object BookPreviewData {
    private val appSec = Book(BookSlug("alice-bob-appsec"), "Alice and Bob Learn Application Security")

    val book = BookUiState.of(
        BookChapters(
            book = appSec,
            chapters = listOf(
                chapter(1, "Introduction", NoteTally(6, 6, Duration.ZERO), isReadingOnly = true),
                chapter(2, "CHAPTER 2: Security basics", NoteTally(33, 33, Duration.ZERO)),
                chapter(3, "CHAPTER 3: Input handling", NoteTally(0, 28, 25.minutes), isKnown = true),
                chapter(4, "CHAPTER 4: Cross-site scripting", NoteTally(5, 8, 3.minutes)),
                chapter(5, "CHAPTER 5: Authentication", NoteTally(0, 41, 38.minutes)),
                chapter(6, "CHAPTER 6: Session management", NoteTally(0, 30, 27.minutes)),
            ),
            completion = BookCompletion(appSec.slug, introduced = 31, remembered = 12, total = 48),
        ),
        undoChapter = null,
    )

    val finished = BookUiState.of(
        BookChapters(
            book = appSec,
            chapters = listOf(
                chapter(1, "Introduction", NoteTally(6, 6, Duration.ZERO), isReadingOnly = true),
                chapter(2, "CHAPTER 2: Security basics", NoteTally(33, 33, Duration.ZERO)),
            ),
            completion = BookCompletion(appSec.slug, introduced = 48, remembered = 48, total = 48),
        ),
        undoChapter = null,
    )

    private fun chapter(
        number: Int,
        title: String,
        tally: NoteTally,
        isReadingOnly: Boolean = false,
        isKnown: Boolean = false,
    ) = ChapterReading(
        chapter = Chapter(appSec.slug, number, title, isReadingOnly),
        tally = tally,
        isKnown = isKnown,
        nextNote = NoteId("${appSec.slug.value}/ch$number/n${tally.notesRead}").takeIf { !tally.isFinished },
    )
}
