package com.paulchibamba.margin.data.repository

import com.paulchibamba.margin.data.database.DatabaseTest
import com.paulchibamba.margin.data.pack.FileAssetSource
import com.paulchibamba.margin.data.pack.PackImporter
import com.paulchibamba.margin.data.pack.PackMapper
import com.paulchibamba.margin.data.pack.PackReader
import com.paulchibamba.margin.data.pack.RecordingLogger
import com.paulchibamba.margin.data.startup.StartupInitializer
import com.paulchibamba.margin.domain.feed.FeedResult
import com.paulchibamba.margin.domain.memory.NoFuzz
import com.paulchibamba.margin.domain.model.ChapterRef
import com.paulchibamba.margin.domain.model.PostRole
import com.paulchibamba.margin.domain.repository.Clock
import com.paulchibamba.margin.domain.signals.AnswerOutcome
import com.paulchibamba.margin.domain.signals.PostExit
import com.paulchibamba.margin.domain.usecase.FeedStateLock
import com.paulchibamba.margin.domain.usecase.FeedStateSource
import com.paulchibamba.margin.domain.usecase.GetNextPost
import com.paulchibamba.margin.domain.usecase.LearningEngines
import com.paulchibamba.margin.domain.usecase.LibraryLoader
import com.paulchibamba.margin.domain.usecase.RecordPostExit
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assume.assumeTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.time.Instant
import java.time.ZoneOffset
import kotlin.random.Random
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue
import kotlin.time.Duration.Companion.seconds

@RunWith(RobolectricTestRunner::class)
class RealPackRepositoryTest : DatabaseTest() {

    private fun startup(): StartupInitializer {
        val assets = FileAssetSource.ofRealPack()
        assumeTrue("No content pack in content/pack: run scripts/sync-content-pack.sh", assets != null)
        return StartupInitializer(PackImporter(PackReader(assets!!), PackMapper(), database, RecordingLogger()))
    }

    @Test
    fun `every post in the real pack decodes into its format`() = runTest {
        val content = RoomContentRepository(database, startup())

        val posts = content.posts()

        assertEquals(database.contentDao().posts().size, posts.size)
        assertTrue(posts.all { post -> post.content.format.name.lowercase() in PACK_FORMATS })
        assertEquals(content.books().size, content.chapters().map { it.bookSlug }.distinct().size)
    }

    @Test
    fun `the feed runs on Room and survives a reload`() = runTest {
        val startup = startup()
        val content = RoomContentRepository(database, startup)
        val progress = RoomProgressRepository(database)
        val settings = RoomSettingsRepository(database, startup) {}
        val clock = object : Clock {
            var instant = Instant.parse("2026-10-01T08:00:00Z")
            override fun now() = instant.also { instant = instant.plusSeconds(7) }
            override fun zone() = ZoneOffset.UTC
        }
        val engines = LearningEngines(Random(seed = 1), NoFuzz)
        val lock = FeedStateLock()
        val stateSource = FeedStateSource(progress, settings, engines)
        val getNextPost = GetNextPost(LibraryLoader(content, progress, settings), stateSource, progress, settings,
            engines, clock, lock)
        val recordPostExit = RecordPostExit(stateSource, progress, settings, engines, clock, lock)

        content.chapters().filter { it.number <= 6 }.forEach { chapter ->
            progress.markChapterKnown(ChapterRef(chapter.bookSlug, chapter.number), clock.now())
        }

        repeat(12) {
            val item = assertIs<FeedResult.Next>(getNextPost()).item
            val answer = if (item.post.role == PostRole.TEST) AnswerOutcome.Correct else null
            recordPostExit(item.post, PostExit(5.seconds, isEngaged = true, answer = answer))
        }

        val state = progress.loadFeedState()!!
        assertEquals(12, state.step)
        assertTrue(state.conceptProgress.values.any { it.isIntroduced })
        assertEquals(12, state.history.size)
        assertEquals(12, progress.observeActivity().first().sumOf { it.postsSeen })
    }

    private companion object {
        val PACK_FORMATS = setOf(
            "carousel", "fact", "tip", "analogy", "dialogue", "versus", "myth", "checklist", "code_example", "meme",
            "mcq", "true_false", "recall", "fill_blank", "spot_bug", "scenario",
        )
    }
}
