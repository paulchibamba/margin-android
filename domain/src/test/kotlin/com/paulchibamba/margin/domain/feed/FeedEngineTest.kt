package com.paulchibamba.margin.domain.feed

import com.paulchibamba.margin.domain.memory.CardState
import com.paulchibamba.margin.domain.memory.FsrsParameters
import com.paulchibamba.margin.domain.memory.FsrsScheduler
import com.paulchibamba.margin.domain.memory.NoFuzz
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue

class FeedEngineTest {

    private fun engine(seed: Int = 42) =
        FeedEngine(FeedConfig(), FsrsScheduler(FsrsParameters.Default, NoFuzz), Random(seed))

    @Test
    fun `the feed starts with the first meme five to nine posts away`() {
        assertTrue(engine().startingState().rewardAtStep in 5..9)
    }

    @Test
    fun `with nothing active and nothing introduced, you are caught up`() {
        val library = libraryWith(settings = defaultSettings.map { it.copy(isActive = false) })

        assertEquals(FeedResult.CaughtUp, engine().next(library, freshState, now))
    }

    @Test
    fun `with nothing read, the feed opens with a preview`() {
        val result = engine().next(libraryWith(), freshState, now)

        val next = assertIs<FeedResult.Next>(result)
        assertEquals(CandidateSource.PREVIEW, next.item.source)
        assertEquals(1, next.state.step)
    }

    @Test
    fun `a new concept arrives with its memory and why it was chosen`() {
        val library = libraryWith(readNotes = setOf(noteId(appSec, 1, 0)))

        val item = assertIs<FeedResult.Next>(engine().next(library, freshState.copy(lastPreviewAtStep = 0), now)).item

        assertEquals(CandidateSource.NEW, item.source)
        assertEquals(CardState.NEW, item.memory?.state)
        assertTrue(item.poolSize >= 1)
        assertTrue(item.appliedFilters.isNotEmpty())
        assertEquals(item.score.total, item.score.parts.values.sum())
    }

    @Test
    fun `the same seed gives the same feed`() {
        assertEquals(postIdsFrom(engine(seed = 7)), postIdsFrom(engine(seed = 7)))
    }

    @Test
    fun `across a session a format only repeats when format variety had to relax`() {
        val items = runSession(engine(), steps = 30)

        val repeats = items.zipWithNext().filter { (previous, next) -> previous.post.format == next.post.format }
        val formats = items.map { it.post.format }
        assertTrue(repeats.all { (_, next) -> "format variety" !in next.appliedFilters }, "formats: $formats")
    }

    private fun postIdsFrom(engine: FeedEngine) = runSession(engine, steps = 20).map { it.post.id }

    private fun runSession(engine: FeedEngine, steps: Int): List<FeedItem> {
        val library = libraryWith(readNotes = readingEverything())
        var state = engine.startingState()
        return buildList {
            repeat(steps) {
                val next = engine.next(library, state, now) as? FeedResult.Next ?: return@buildList
                add(next.item)
                state = next.state
            }
        }
    }
}
