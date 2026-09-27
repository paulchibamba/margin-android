package com.paulchibamba.margin.feature.read.home

import com.paulchibamba.margin.domain.model.Book
import com.paulchibamba.margin.domain.model.BookSlug
import com.paulchibamba.margin.domain.model.NoteId
import com.paulchibamba.margin.domain.model.NoteOutline
import com.paulchibamba.margin.domain.model.NotePosition
import com.paulchibamba.margin.domain.model.Priority
import com.paulchibamba.margin.domain.usecase.BookReading
import com.paulchibamba.margin.domain.usecase.ContinueNote
import com.paulchibamba.margin.domain.usecase.NoteTally
import com.paulchibamba.margin.domain.usecase.PlaceInChapter
import com.paulchibamba.margin.domain.usecase.ReadingHome
import kotlin.time.Duration.Companion.minutes

object ReadHomePreviewData {
    private val appSec = Book(BookSlug("alice-bob-appsec"), "Alice and Bob Learn Application Security")
    private val grokking = Book(BookSlug("grokking-web-app-security"), "Grokking Web Application Security")
    private val aiSecurity = Book(BookSlug("practical-ai-security"), "Practical AI Security")

    val continueNote = ContinueNote(
        outline = NoteOutline(
            id = NoteId("alice-bob-appsec/ch4/n5"),
            bookSlug = appSec.slug,
            position = NotePosition(chapter = 4, order = 5),
            section = "Reflected vs stored XSS",
            readingTime = 1.minutes,
        ),
        chapterTitle = "XSS",
        place = PlaceInChapter(order = 5, noteCount = 8),
        unlockedPosts = 4,
    )

    val threeBooks = ReadHomeUiState.of(
        ReadingHome(
            continueNote = continueNote,
            books = listOf(
                BookReading(appSec, NoteTally(204, 330, 126.minutes), isActive = true, Priority.MAIN),
                BookReading(grokking, NoteTally(51, 290, 240.minutes), isActive = true, Priority.NORMAL),
                BookReading(aiSecurity, NoteTally(0, 616, 540.minutes), isActive = false, Priority.LOW),
            ),
        ),
        streak = 7,
    )

    val oneBook = ReadHomeUiState.of(
        ReadingHome(
            continueNote = continueNote,
            books = listOf(BookReading(appSec, NoteTally(204, 330, 126.minutes), isActive = true, Priority.MAIN)),
        ),
        streak = 3,
    )
}
