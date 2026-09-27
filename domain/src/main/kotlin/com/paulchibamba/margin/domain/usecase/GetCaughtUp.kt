package com.paulchibamba.margin.domain.usecase

import com.paulchibamba.margin.domain.feed.FeedState
import com.paulchibamba.margin.domain.model.BookSlug
import com.paulchibamba.margin.domain.model.NoteOutline
import com.paulchibamba.margin.domain.progression.ReadingOrder
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
    private val unlockedPosts: UnlockedPostCount,
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
        return NextNote(outline, chapterTitleOf(outline), unlockedPosts.at(outline, state))
    }

    private suspend fun booksToReadNext(): List<BookSlug> =
        ReadingOrder(content.books().map { it.slug }, settings.bookSettings()).booksToReadNext()

    private fun noteAfterFrontier(book: BookSlug, outlines: List<NoteOutline>, reading: ReadingProgress) =
        reading.frontierOf(book).let { frontier ->
            outlines.firstOrNull { it.bookSlug == book && (frontier == null || it.position > frontier) }
        }

    private suspend fun chapterTitleOf(outline: NoteOutline): String = content.chapters()
        .firstOrNull { it.bookSlug == outline.bookSlug && it.number == outline.position.chapter }
        ?.title.orEmpty()

    private fun nextReviewIn(state: FeedState): Duration? {
        val now = clock.now()
        val nextDue = state.conceptProgress.values.mapNotNull { it.card?.due }.filter { it.isAfter(now) }.minOrNull()
        return nextDue?.let { due -> JavaDuration.between(now, due).toKotlinDuration() }
    }
}
