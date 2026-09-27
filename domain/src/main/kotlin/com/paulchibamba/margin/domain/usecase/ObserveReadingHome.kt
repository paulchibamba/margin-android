package com.paulchibamba.margin.domain.usecase

import com.paulchibamba.margin.domain.model.Book
import com.paulchibamba.margin.domain.model.BookSettings
import com.paulchibamba.margin.domain.model.BookSlug
import com.paulchibamba.margin.domain.model.ChapterRef
import com.paulchibamba.margin.domain.model.NoteId
import com.paulchibamba.margin.domain.model.NoteOutline
import com.paulchibamba.margin.domain.model.Priority
import com.paulchibamba.margin.domain.progression.ReadingOrder
import com.paulchibamba.margin.domain.progression.ReadingState
import com.paulchibamba.margin.domain.repository.ContentRepository
import com.paulchibamba.margin.domain.repository.ProgressRepository
import com.paulchibamba.margin.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

class ObserveReadingHome @Inject constructor(
    private val content: ContentRepository,
    private val progress: ProgressRepository,
    private val settings: SettingsRepository,
    private val unlockedPosts: UnlockedPostCount,
) {
    operator fun invoke(): Flow<ReadingHome> =
        combine(progress.observeReading(), settings.observeBookSettings()) { reading, bookSettings ->
            homeFor(reading, bookSettings)
        }

    private suspend fun homeFor(reading: ReadingState, bookSettings: List<BookSettings>): ReadingHome {
        val notesByBook = content.noteOutlines().sortedBy(NoteOutline::position).groupBy(NoteOutline::bookSlug)
        val books = content.books().associateBy { it.slug }
        val order = ReadingOrder(books.keys.toList(), bookSettings)
        val settingsByBook = bookSettings.associateBy(BookSettings::bookSlug)
        return ReadingHome(
            continueNote = continueOutline(reading, notesByBook, order)?.let { continueNoteAt(it, notesByBook) },
            books = order.activeFirst().map { slug ->
                bookReadingOf(books.getValue(slug), settingsByBook[slug], notesByBook[slug].orEmpty(), reading)
            },
        )
    }

    private fun bookReadingOf(book: Book, settings: BookSettings?, notes: List<NoteOutline>, reading: ReadingState) =
        BookReading(
            book = book,
            tally = NoteTally.of(notes, reading.readNotes),
            isActive = settings?.isActive == true,
            priority = settings?.priority ?: Priority.LOW,
        )

    private fun continueOutline(
        reading: ReadingState,
        notesByBook: Map<BookSlug, List<NoteOutline>>,
        order: ReadingOrder,
    ): NoteOutline? {
        val isUnread = { outline: NoteOutline -> isUnread(outline, reading) }
        return reading.lastNote?.let { last -> firstUnreadFrom(last, notesByBook, isUnread) }
            ?: order.booksToReadNext().firstNotNullOfOrNull { book -> notesByBook[book]?.firstOrNull(isUnread) }
    }

    private fun firstUnreadFrom(
        note: NoteId,
        notesByBook: Map<BookSlug, List<NoteOutline>>,
        isUnread: (NoteOutline) -> Boolean,
    ): NoteOutline? {
        val bookNotes = notesByBook[note.bookSlug].orEmpty()
        val start = bookNotes.indexOfFirst { it.id == note }
        return if (start < 0) null else bookNotes.drop(start).firstOrNull(isUnread)
    }

    private fun isUnread(outline: NoteOutline, reading: ReadingState): Boolean =
        outline.id !in reading.readNotes && !reading.isKnown(ChapterRef(outline.bookSlug, outline.position.chapter))

    private suspend fun continueNoteAt(outline: NoteOutline, notesByBook: Map<BookSlug, List<NoteOutline>>) =
        ContinueNote(
            outline = outline,
            chapterTitle = chapterTitleOf(outline),
            place = PlaceInChapter.of(outline.id, notesByBook.getValue(outline.bookSlug)),
            unlockedPosts = unlockedPosts.at(outline, progress.loadFeedState()),
        )

    private suspend fun chapterTitleOf(outline: NoteOutline): String = content.chapters()
        .firstOrNull { it.bookSlug == outline.bookSlug && it.number == outline.position.chapter }
        ?.title.orEmpty()
}
