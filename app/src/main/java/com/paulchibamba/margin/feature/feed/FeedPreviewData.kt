package com.paulchibamba.margin.feature.feed

import com.paulchibamba.margin.designsystem.Skin
import com.paulchibamba.margin.domain.actions.PostAction
import com.paulchibamba.margin.domain.actions.PostViewState
import com.paulchibamba.margin.domain.feed.CandidateSource
import com.paulchibamba.margin.domain.feed.FeedItem
import com.paulchibamba.margin.domain.feed.MemorySnapshot
import com.paulchibamba.margin.domain.feed.ranking.ScoreBreakdown
import com.paulchibamba.margin.domain.feed.ranking.ScorePart
import com.paulchibamba.margin.domain.memory.CardState
import com.paulchibamba.margin.domain.model.BookSlug
import com.paulchibamba.margin.domain.model.ConceptId
import com.paulchibamba.margin.domain.model.NoteId
import com.paulchibamba.margin.domain.model.NoteOutline
import com.paulchibamba.margin.domain.model.NotePosition
import com.paulchibamba.margin.domain.model.Post
import com.paulchibamba.margin.domain.model.PostContent
import com.paulchibamba.margin.domain.model.PostId
import com.paulchibamba.margin.domain.rewards.BookCompletion
import com.paulchibamba.margin.domain.usecase.CaughtUp
import com.paulchibamba.margin.domain.usecase.NextNote
import com.paulchibamba.margin.domain.usecase.PostContext
import com.paulchibamba.margin.domain.usecase.ReadingAhead
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes

internal object FeedPreviewData {
    private val book = BookSlug("alice-bob-appsec")
    private val note = NoteId("alice-bob-appsec/ch04/n003")

    val item = FeedItem(
        post = Post(
            id = PostId("alice-bob-appsec/ch3/c2/tip"),
            conceptId = ConceptId("alice-bob-appsec/ch3/c2"),
            bookSlug = book,
            content = PostContent.Tip(
                title = "Never trust the client.",
                text = "Client-side checks are for UX, not security. Anyone with an intercepting proxy skips them.",
            ),
        ),
        source = CandidateSource.REVIEW,
        score = ScoreBreakdown(
            mapOf(ScorePart.SOURCE to 1.3, ScorePart.URGENCY to 0.5, ScorePart.FORMAT to 0.41,
                ScorePart.NOVELTY to 0.2, ScorePart.JITTER to 0.02),
        ),
        rank = 1,
        poolSize = 14,
        wasExploration = false,
        appliedFilters = listOf("concept spacing", "tests in a row"),
        memory = MemorySnapshot(CardState.REVIEW, 6.2, 5.1, recall = 0.83, dueIn = 2.days, reps = 4, lapses = 0),
    )

    val context = PostContext(
        conceptTitle = "Server-side validation",
        bookTitle = "Alice & Bob Learn AppSec",
        chapterNumber = 3,
        chapterTitle = "CHAPTER 3: Input",
        completion = BookCompletion(book, introduced = 12, remembered = 3, total = 48),
        sourceNote = note,
    )

    val caughtUp = CaughtUp(
        nextNote = NextNote(
            outline = NoteOutline(note, book, NotePosition(4, 3), "Reflected vs stored XSS", 1.minutes),
            chapterTitle = "CHAPTER 4: XSS",
            unlockedPosts = 4,
        ),
        nextReviewIn = 2.hours,
    )

    fun page(skin: Skin, lost: Boolean = false) = FeedPage(
        item = item,
        context = context,
        skin = skin,
        viewState = if (lost) PostViewState(setOf(PostAction.LOST)) else PostViewState(),
    )

    fun page(skin: Skin, content: PostContent, source: CandidateSource, readingAhead: ReadingAhead?) = FeedPage(
        item = item.copy(post = item.post.copy(content = content), source = source),
        context = context.copy(readingAhead = readingAhead),
        skin = skin,
    )
}
