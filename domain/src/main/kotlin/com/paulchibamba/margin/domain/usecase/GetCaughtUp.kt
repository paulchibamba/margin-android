package com.paulchibamba.margin.domain.usecase

import com.paulchibamba.margin.domain.feed.FeedState
import com.paulchibamba.margin.domain.model.BookSettings
import com.paulchibamba.margin.domain.model.BookSlug
import com.paulchibamba.margin.domain.model.Concept
import com.paulchibamba.margin.domain.model.NoteOutline
import com.paulchibamba.margin.domain.progression.ReadingOnlyChapters
import com.paulchibamba.margin.domain.progression.ReadingProgress
import com.paulchibamba.margin.domain.repository.Clock
import com.paulchibamba.margin.domain.repository.ContentRepository
import com.paulchibamba.margin.domain.repository.ProgressRepository
import com.paulchibamba.margin.domain.repository.SettingsRepository
import javax.inject.Inject
import kotlin.time.Duration
import kotlin.time.toKotlinDuration
import java.time.Duration as JavaDuration

class GetCaughtUp @Inject constructor(
    private val content: ContentRepository,
    private val progress: ProgressRepository,
    private val settings: SettingsRepository,
    private val clock: Clock,
) {
    suspend operator fun invoke(): CaughtUp {
        val state = progress.loadFeedState()
        return CaughtUp(nextNote = nextNote(state), nextReviewIn = state?.let(::nextReviewIn))
    }

    private suspend fun nextNote(state: FeedState?): NextNote? {
        val outlines = content.noteOutlines().sortedBy(NoteOutline::position)
        val reading = progress.reading().progressWith(outlines)
        val outline = booksToReadNext().firstNotNullOfOrNull { book -> noteAfterFrontier(book, outlines, reading) }
            ?: return null
        return NextNote(outline, chapterTitleOf(outline), unlockedPostsAt(outline, state))
    }

    private suspend fun booksToReadNext(): List<BookSlug> {
        val bookOrder = content.books().map { it.slug }
        val active = settings.bookSettings().filter(BookSettings::isActive)
            .sortedWith(compareBy<BookSettings> { it.priority }.thenBy { bookOrder.indexOf(it.bookSlug) })
            .map(BookSettings::bookSlug)
        return active.ifEmpty { bookOrder }
    }

    private fun noteAfterFrontier(book: BookSlug, outlines: List<NoteOutline>, reading: ReadingProgress) =
        reading.frontierOf(book).let { frontier ->
            outlines.firstOrNull { it.bookSlug == book && (frontier == null || it.position > frontier) }
        }

    private suspend fun chapterTitleOf(outline: NoteOutline): String = content.chapters()
        .firstOrNull { it.bookSlug == outline.bookSlug && it.number == outline.position.chapter }
        ?.title.orEmpty()

    private suspend fun unlockedPostsAt(outline: NoteOutline, state: FeedState?): Int {
        val readingOnly = settings.readingOnlyChapters()
        val unlocked = content.concepts()
            .filter { concept -> isUnlockedBy(concept, outline, readingOnly) && state?.isIntroduced(concept) != true }
            .map { it.id }
            .toSet()
        return content.posts().count { it.conceptId in unlocked }
    }

    private fun isUnlockedBy(concept: Concept, outline: NoteOutline, readingOnly: ReadingOnlyChapters): Boolean =
        concept.bookSlug == outline.bookSlug && concept.position == outline.position && concept !in readingOnly

    private fun nextReviewIn(state: FeedState): Duration? {
        val now = clock.now()
        val nextDue = state.conceptProgress.values.mapNotNull { it.card?.due }.filter { it.isAfter(now) }.minOrNull()
        return nextDue?.let { due -> JavaDuration.between(now, due).toKotlinDuration() }
    }
}
