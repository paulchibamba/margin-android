package com.paulchibamba.margin.domain.usecase

import com.paulchibamba.margin.domain.feed.appSec
import com.paulchibamba.margin.domain.feed.cia
import com.paulchibamba.margin.domain.feed.defenceInDepth
import com.paulchibamba.margin.domain.feed.introducedProgress
import com.paulchibamba.margin.domain.feed.leastPrivilege
import com.paulchibamba.margin.domain.feed.noteId
import com.paulchibamba.margin.domain.feed.now
import com.paulchibamba.margin.domain.feed.sameOrigin
import com.paulchibamba.margin.domain.feed.tipOf
import com.paulchibamba.margin.domain.feed.withIntroduced
import com.paulchibamba.margin.domain.model.PostContent
import com.paulchibamba.margin.domain.progression.ReadingState
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes

class FeedPageUseCasesTest {

    private val fixture = UseCaseFixture()

    @Test
    fun `a post is described by its concept, book, chapter and the book's introduced count`() = runTest {
        fixture.progress.feedState.value = startingState().withIntroduced(cia)

        val context = fixture.describePost(tipOf(cia))

        assertEquals(cia.title, context.conceptTitle)
        assertEquals(appSec.title, context.bookTitle)
        assertEquals(1, context.chapterNumber)
        assertEquals("Chapter 1", context.chapterTitle)
        assertEquals(1 to 3, context.completion.introduced to context.completion.total)
        assertEquals(cia.sourceNoteId, context.sourceNote)
    }

    @Test
    fun `a from-the-book post reads its own note`() = runTest {
        val sourceNote = noteId(appSec, chapter = 3, order = 0)
        val post = tipOf(cia).copy(content = PostContent.Source("Section", "The book's words.", sourceNote))

        assertEquals(sourceNote, fixture.describePost(post).sourceNote)
    }

    @Test
    fun `a concept past the frontier counts the notes and minutes left to read up to it`() = runTest {
        fixture.progress.reading.value = ReadingState(readNotes = setOf(cia.sourceNoteId!!))

        val ahead = fixture.describePost(tipOf(defenceInDepth)).readingAhead

        assertEquals(ReadingAhead(leastPrivilege.sourceNoteId!!, noteCount = 3, readingTime = 3.minutes), ahead)
    }

    @Test
    fun `a concept already read up to has nothing ahead`() = runTest {
        fixture.progress.reading.value = ReadingState(readNotes = setOf(leastPrivilege.sourceNoteId!!))

        assertNull(fixture.describePost(tipOf(cia)).readingAhead)
    }

    @Test
    fun `with nothing read the first concept is one note ahead`() = runTest {
        assertEquals(1, fixture.describePost(tipOf(cia)).readingAhead?.noteCount)
    }

    @Test
    fun `with nothing read the next note is the main book's first note`() = runTest {
        val caughtUp = fixture.getCaughtUp()

        assertEquals(cia.sourceNoteId, caughtUp.nextNote?.outline?.id)
        assertEquals(5, caughtUp.nextNote?.unlockedPosts)
    }

    @Test
    fun `the next note is the one after the main book's frontier`() = runTest {
        fixture.progress.reading.value = ReadingState(readNotes = setOf(cia.sourceNoteId!!))

        assertEquals(leastPrivilege.sourceNoteId, fixture.getCaughtUp().nextNote?.outline?.id)
    }

    @Test
    fun `when the main book is finished the next note comes from the next active book`() = runTest {
        val appSecNotes = setOf(noteId(appSec, 9, 99))
        fixture.progress.reading.value = ReadingState(readNotes = appSecNotes)

        assertEquals(sameOrigin.sourceNoteId, fixture.getCaughtUp().nextNote?.outline?.id)
    }

    @Test
    fun `the next review is due when the earliest future card is`() = runTest {
        val soon = now.plusSeconds(3_600)
        fixture.progress.feedState.value = startingState()
            .withIntroduced(cia, progress = introducedProgress(due = now.minusSeconds(60)))
            .withIntroduced(leastPrivilege, progress = introducedProgress(due = soon))
            .withIntroduced(sameOrigin, progress = introducedProgress(due = soon.plusSeconds(60)))

        assertEquals(1.hours, fixture.getCaughtUp().nextReviewIn)
    }

    @Test
    fun `with nothing introduced there is no next review`() = runTest {
        assertNull(fixture.getCaughtUp().nextReviewIn)
    }

    private fun startingState() = fixture.engines.feedEngine(0.9).startingState()
}
