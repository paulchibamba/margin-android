package com.paulchibamba.margin.domain.progress

import com.paulchibamba.margin.domain.actions.ActionLogEntry
import com.paulchibamba.margin.domain.feed.FeedState
import com.paulchibamba.margin.domain.feed.ReviewLogEntry
import com.paulchibamba.margin.domain.model.Book
import com.paulchibamba.margin.domain.model.BookSlug
import com.paulchibamba.margin.domain.model.Chapter
import com.paulchibamba.margin.domain.model.Concept
import com.paulchibamba.margin.domain.model.NoteOutline
import com.paulchibamba.margin.domain.model.Post
import com.paulchibamba.margin.domain.model.PostId
import com.paulchibamba.margin.domain.progression.ReadingOnlyChapters
import com.paulchibamba.margin.domain.progression.ReadingState
import com.paulchibamba.margin.domain.tracking.LoggedEvent
import java.time.Instant

data class ProgressSources(
    val books: List<Book>,
    val chapters: List<Chapter>,
    val concepts: List<Concept>,
    val posts: List<Post>,
    val noteOutlines: List<NoteOutline>,
    val activeBooks: Set<BookSlug>,
    val readingOnlyChapters: ReadingOnlyChapters,
    val reading: ReadingState,
    val feedState: FeedState,
    val reviews: List<ReviewLogEntry>,
    val actions: List<ActionLogEntry>,
    val firstSeen: Map<PostId, Instant>,
    val events: List<LoggedEvent>,
    val history: RewardHistory,
)
