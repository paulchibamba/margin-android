package com.paulchibamba.margin.domain.usecase

import com.paulchibamba.margin.domain.feed.CandidateSource
import com.paulchibamba.margin.domain.feed.FeedResult
import com.paulchibamba.margin.domain.feed.aiSecurity
import com.paulchibamba.margin.domain.feed.appSec
import com.paulchibamba.margin.domain.feed.cia
import com.paulchibamba.margin.domain.feed.grokking
import com.paulchibamba.margin.domain.feed.learnedProgress
import com.paulchibamba.margin.domain.feed.mcqOf
import com.paulchibamba.margin.domain.feed.withIntroduced
import com.paulchibamba.margin.domain.memory.Rating
import com.paulchibamba.margin.domain.model.ChapterRef
import com.paulchibamba.margin.domain.model.DarkMode
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class SettingsUseCasesTest {

    private val fixture = UseCaseFixture()

    @Test
    fun `the learning settings list every book with its chapters and reading-only flags`() = runTest {
        val settings = fixture.observeLearningSettings().first()

        assertEquals(listOf(appSec, grokking, aiSecurity), settings.books.map { it.book })
        assertEquals(2, settings.activeCount)
        val appSecChapters = settings.books.first().chapters
        assertEquals(listOf(false, true, false), appSecChapters.map { it.isReadingOnly })
    }

    @Test
    fun `a book can be toggled only while one to three books stay active`() = runTest {
        val settings = fixture.observeLearningSettings().first()

        assertTrue(settings.books.all(settings::canToggle))
        val onlyAppSec = settings.copy(books = settings.books.map { it.withActive(it.book == appSec) })
        assertFalse(onlyAppSec.canToggle(onlyAppSec.books.first()))
        val allActive = settings.copy(books = settings.books.map { it.withActive(true) })
        assertTrue(allActive.canToggle(allActive.books.first()))
    }

    @Test
    fun `retention is rounded to two places and kept in its range`() = runTest {
        fixture.setDesiredRetention(0.8712)
        assertEquals(0.87, fixture.settings.retention.value)

        fixture.setDesiredRetention(0.99)
        assertEquals(0.95, fixture.observeLearningSettings().first().desiredRetention)
    }

    @Test
    fun `the saved retention sets the scheduler's intervals`() = runTest {
        fixture.progress.feedState.value = fixture.engines.feedEngine(0.9).startingState()
            .withIntroduced(cia, progress = learnedProgress())

        fixture.setDesiredRetention(0.8)
        val relaxed = fixture.previewIntervals(mcqOf(cia)).getValue(Rating.GOOD)
        fixture.setDesiredRetention(0.95)
        val strict = fixture.previewIntervals(mcqOf(cia)).getValue(Rating.GOOD)

        assertTrue(strict < relaxed)
    }

    @Test
    fun `a chapter can be made reading-only and back`() = runTest {
        val chapterThree = ChapterRef(appSec.slug, 3)

        fixture.setChapterReadingOnly(chapterThree, isReadingOnly = true)
        assertEquals(setOf(2, 3), fixture.settings.readingOnly.value[appSec.slug])

        fixture.setChapterReadingOnly(chapterThree, isReadingOnly = false)
        assertEquals(setOf(2), fixture.settings.readingOnly.value[appSec.slug])
    }

    @Test
    fun `a deactivated book gives no new concepts to the next post`() = runTest {
        fixture.markChapterKnown(ChapterRef(appSec.slug, 1))
        val startingState = fixture.engines.feedEngine(0.9).startingState().copy(lastPreviewAtStep = 0)
        val appSecSettings = fixture.settings.bookSettings().first { it.bookSlug == appSec.slug }

        fixture.updateBookSettings(appSecSettings.copy(isActive = false))
        fixture.progress.feedState.value = startingState

        val item = (fixture.getNextPost() as? FeedResult.Next)?.item
        assertFalse(item?.source == CandidateSource.NEW && item.post.bookSlug == appSec.slug)
    }

    @Test
    fun `dark mode starts off and a saved choice reaches the learning settings`() = runTest {
        assertEquals(DarkMode.OFF, fixture.observeLearningSettings().first().darkMode)

        fixture.setDarkMode(DarkMode.ALWAYS)

        assertEquals(DarkMode.ALWAYS, fixture.observeLearningSettings().first().darkMode)
        assertEquals(DarkMode.ALWAYS, fixture.observeDarkMode().first())
    }

    private fun BookLearningSettings.withActive(isActive: Boolean) =
        copy(settings = settings.copy(isActive = isActive))
}
