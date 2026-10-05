package com.paulchibamba.margin.domain.simulation

import com.paulchibamba.margin.domain.actions.PostAction
import com.paulchibamba.margin.domain.actions.PostActionHandler
import com.paulchibamba.margin.domain.feed.FeedConfig
import com.paulchibamba.margin.domain.feed.FeedEngine
import com.paulchibamba.margin.domain.feed.FeedItem
import com.paulchibamba.margin.domain.feed.FeedResult
import com.paulchibamba.margin.domain.feed.FeedState
import com.paulchibamba.margin.domain.feed.LearningLibrary
import com.paulchibamba.margin.domain.feed.PostExitHandler
import com.paulchibamba.margin.domain.memory.FsrsScheduler
import com.paulchibamba.margin.domain.model.Note
import com.paulchibamba.margin.domain.progress.GeneratedPost
import com.paulchibamba.margin.domain.progression.PreviewWindow
import com.paulchibamba.margin.domain.progression.ReadingProgress
import com.paulchibamba.margin.domain.signals.EngagementCalculator
import com.paulchibamba.margin.domain.signals.GradeMapper
import java.time.Instant
import kotlin.random.Random
import kotlin.time.Duration.Companion.seconds
import kotlin.time.toJavaDuration

class FeedSimulation(
    private val pack: TestPack,
    readUpToChapter: Int,
    seed: Int,
    private val exitStrategy: ExitStrategy = ExitStrategy.MostlyRight,
    private val generatedPosts: List<GeneratedPost> = emptyList(),
) {
    private val random = Random(seed)
    private val scheduler = FsrsScheduler()
    private val engine = FeedEngine(FeedConfig(), scheduler, random)
    private val actionHandler = PostActionHandler(scheduler)
    private val exitHandler = PostExitHandler(EngagementCalculator(), GradeMapper(), scheduler)
    private val library = libraryReadUpTo(readUpToChapter)

    var now: Instant = START
        private set
    var state: FeedState = engine.startingState()
        private set

    fun next(): FeedItem? {
        val result = engine.next(library, state, now)
        now += TIME_PER_POST
        if (result !is FeedResult.Next) return null
        state = result.state
        return result.item
    }

    fun see(item: FeedItem) {
        state = exitHandler.apply(state, item.post, exitStrategy.exitFor(item, random), now).state
    }

    fun act(item: FeedItem, action: PostAction) {
        state = actionHandler.apply(state, item.post, action, now).state
    }

    fun scroll(posts: Int): List<FeedItem> = generateSequence { next()?.also(::see) }.take(posts).toList()

    private fun libraryReadUpTo(chapter: Int) = LearningLibrary(
        books = pack.books,
        concepts = pack.concepts,
        posts = pack.posts,
        sourceNotes = pack.notes.associateBy(Note::id),
        bookSettings = pack.bookSettings,
        readingOnlyChapters = pack.readingOnlyChapters,
        readingProgress = ReadingProgress(readNotes = notesUpTo(chapter), knownChapterEnds = emptySet()),
        previewWindow = PreviewWindow(pack.previewNotesAhead),
        generatedPosts = generatedPosts,
    )

    private fun notesUpTo(chapter: Int) = pack.notes.filter { it.position.chapter <= chapter }.map(Note::id).toSet()

    companion object {
        const val EVERYTHING = 99
        private val START = Instant.parse("2026-10-01T08:00:00Z")
        private val TIME_PER_POST = 7.seconds.toJavaDuration()
    }
}
