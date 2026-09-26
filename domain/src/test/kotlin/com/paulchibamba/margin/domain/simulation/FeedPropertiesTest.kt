package com.paulchibamba.margin.domain.simulation

import com.paulchibamba.margin.domain.actions.PostAction
import com.paulchibamba.margin.domain.feed.CandidateSource
import com.paulchibamba.margin.domain.feed.FeedItem
import com.paulchibamba.margin.domain.feed.PostExitHandler
import com.paulchibamba.margin.domain.memory.FsrsScheduler
import com.paulchibamba.margin.domain.model.BookSlug
import com.paulchibamba.margin.domain.model.Format
import com.paulchibamba.margin.domain.model.PostRole
import com.paulchibamba.margin.domain.signals.EngagementCalculator
import com.paulchibamba.margin.domain.signals.GradeMapper
import com.paulchibamba.margin.domain.signals.PostExit
import org.junit.Assume.assumeTrue
import org.junit.Before
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.time.Duration.Companion.milliseconds

class FeedPropertiesTest {

    private lateinit var pack: TestPack

    @Before
    fun loadPack() {
        val loaded = TestPackLoader.pack
        assumeTrue("No content pack in content/pack: run scripts/sync-content-pack.sh", loaded != null)
        pack = loaded!!
    }

    private fun simulation(readUpToChapter: Int, seed: Int, exits: ExitStrategy = ExitStrategy.MostlyRight) =
        FeedSimulation(pack, readUpToChapter, seed, exits)

    @Test
    fun `no format repeats and no concept returns within 3 posts across 40 posts by 50 runs`() {
        val violations = (1..50).sumOf { seed ->
            spacingViolations(simulation(FeedSimulation.EVERYTHING, seed).scroll(posts = 40))
        }

        assertEquals(0, violations)
    }

    @Test
    fun `every persona keeps the spacing rules across 40 posts by 20 runs`() {
        Persona.All.forEach { persona ->
            val violations = (1..20).sumOf { seed ->
                spacingViolations(simulation(FeedSimulation.EVERYTHING, seed, persona).scroll(posts = 40))
            }

            assertEquals(0, violations, "${persona.name} broke the spacing rules")
        }
    }

    @Test
    fun `every persona learns to show its hated formats less`() {
        Persona.All.forEach { persona ->
            val simulation = simulation(FeedSimulation.EVERYTHING, seed = 1, persona).also { it.scroll(posts = 60) }

            val affinity = simulation.state.affinity
            persona.hates.filter(affinity.values::containsKey).forEach { format ->
                assertTrue(affinity.valueOf(format) < 0.5, "${persona.name} still likes $format")
            }
        }
    }

    @Test
    fun `after Lost the concept returns as a teach post in at least 85 percent of 300 runs`() {
        val returns = (1..300).mapNotNull { seed -> roleOfReturn(seed, PostAction.LOST) }

        assertTrue(returns.isNotEmpty())
        assertTrue(returns.count { it == PostRole.TEACH } >= 0.85 * returns.size, "teach in $returns")
    }

    @Test
    fun `after Got it the concept returns as a test in at least 95 percent of 300 runs`() {
        val returns = (1..300).mapNotNull { seed -> roleOfReturn(seed, PostAction.GOT) }

        assertTrue(returns.isNotEmpty())
        assertTrue(returns.count { it == PostRole.TEST } >= 0.95 * returns.size, "tests in $returns")
    }

    @Test
    fun `with nothing read the feed shows only previews, then is caught up within 60 posts`() {
        val items = simulation(readUpToChapter = 0, seed = 1).scroll(posts = 60)

        assertEquals(setOf(CandidateSource.PREVIEW), items.map { it.source }.toSet())
        assertTrue(items.size < 60, "still going after ${items.size} posts")
    }

    @Test
    fun `with chapters up to 6 read the main book gets 50 to 70 percent of new concepts`() {
        val shares = (1..30).map { seed ->
            val newBooks = simulation(readUpToChapter = 6, seed).scroll(posts = 30).newConceptBooks()
            newBooks.count { it == pack.mainBook.slug } / newBooks.size.coerceAtLeast(1).toDouble()
        }

        assertTrue(shares.average() in 0.5..0.7, "average share ${shares.average()}")
    }

    @Test
    fun `every active book introduces at least one concept in 40 posts`() {
        val starved = (1..30).sumOf { seed ->
            val introducing = simulation(readUpToChapter = 6, seed).scroll(posts = 40).newConceptBooks().toSet()
            pack.activeBooks.count { book -> book.slug !in introducing }
        }

        assertEquals(0, starved)
    }

    @Test
    fun `inactive books never introduce or preview`() {
        val inactive = pack.inactiveBooks.map { it.slug }.toSet()

        val violations = (1..20).sumOf { seed ->
            simulation(FeedSimulation.EVERYTHING, seed).scroll(posts = 40)
                .count { it.source in INTRODUCING_SOURCES && it.post.bookSlug in inactive }
        }

        assertEquals(0, violations)
    }

    @Test
    fun `a fast skip on a checklist moves its affinity from 0·5 to 0·375`() {
        val simulation = simulation(FeedSimulation.EVERYTHING, seed = 1)
        val checklist = pack.posts.first { it.format == Format.CHECKLIST }
        val exits = PostExitHandler(EngagementCalculator(), GradeMapper(), FsrsScheduler())

        val state = exits.apply(simulation.state, checklist, PostExit(500.milliseconds, false), simulation.now).state

        assertEquals(0.375, state.affinity.valueOf(Format.CHECKLIST), 1e-9)
    }

    private fun roleOfReturn(seed: Int, action: PostAction): PostRole? {
        val simulation = simulation(FeedSimulation.EVERYTHING, seed)
        val first = simulation.next() ?: return null
        simulation.see(first)
        simulation.act(first, action)
        repeat(6) {
            val item = simulation.next() ?: return null
            if (item.post.conceptId == first.post.conceptId) return item.post.role
            simulation.see(item)
        }
        return null
    }

    private fun spacingViolations(items: List<FeedItem>): Int = items.indices.drop(1).count { index ->
        val item = items[index]
        val isFormatRepeat = item.post.format == items[index - 1].post.format
        val recentConcepts = items.subList((index - 3).coerceAtLeast(0), index).map { it.post.conceptId }
        isFormatRepeat || item.post.conceptId in recentConcepts
    }

    private fun List<FeedItem>.newConceptBooks(): List<BookSlug> =
        filter { it.source == CandidateSource.NEW }.map { it.post.bookSlug }

    private companion object {
        val INTRODUCING_SOURCES = setOf(CandidateSource.NEW, CandidateSource.PREVIEW)
    }
}
