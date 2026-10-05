package com.paulchibamba.margin.domain.tracking

import com.paulchibamba.margin.domain.model.NoteId
import com.paulchibamba.margin.domain.usecase.FixedClock
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds
import kotlin.time.toJavaDuration
import kotlinx.coroutines.test.runTest

class NoteAttentionTest {
    private val clock = FixedClock()
    private val recorded = mutableListOf<Event>()
    private val sink = RecordingEventSink()
    private val log = FakeEventLog()
    private val attention = NoteAttention(clock, recorded::add, sink, log).apply { onShown(true) }
    private val note = NoteId("appsec/ch01/n002")

    private fun visit(hasImages: Boolean = true) = NoteVisit(note, words = 300, NoteOpenVia.CHAPTER, hasImages)

    private fun advance(by: Duration) {
        clock.instant = clock.instant + by.toJavaDuration()
    }

    private inline fun <reified T : Event> only(): List<T> = recorded.filterIsInstance<T>()

    @Test
    fun `opening a note counts the earlier opens after writing the buffer`() = runTest {
        log.counts[EventType.NOTE_OPEN to note.value] = 2

        attention.onOpened(visit())

        assertEquals(listOf(Event.NoteOpen(note, NoteOpenVia.CHAPTER, openCount = 3)), only<Event.NoteOpen>())
        assertEquals(1, sink.flushCount)
    }

    @Test
    fun `closing a note records its attention, pace, scrolling and whether it was marked read`() = runTest {
        attention.onOpened(visit())
        attention.onScrolled(ScrollPosition(0, 1000, 4000))
        advance(15.seconds)
        attention.onInput()
        attention.onScrolled(ScrollPosition(3000, 1000, 4000))
        attention.onScrolled(ScrollPosition(2500, 1000, 4000))
        advance(30.seconds)

        attention.onClosed(isMarkedRead = true)

        val exposure = only<Event.NoteExposure>().single()
        assertEquals(35.seconds, exposure.activeTime)
        assertEquals(10.seconds, exposure.idleTime)
        assertEquals(514, exposure.wordsPerMinute)
        assertEquals(100, exposure.maxScrollPercent)
        assertEquals(1, exposure.scrollBacks)
        assertEquals(true, exposure.isMarkedRead)
    }

    @Test
    fun `closing twice records one exposure`() = runTest {
        attention.onOpened(visit())
        attention.onClosed(isMarkedRead = false)
        attention.onClosed(isMarkedRead = false)

        assertEquals(1, only<Event.NoteExposure>().size)
    }

    @Test
    fun `zooming a note with a diagram records an image zoom`() = runTest {
        attention.onOpened(visit(hasImages = true))

        attention.onZoomedIn()

        assertEquals(listOf(Event.ImageZoom(postId = null, noteId = note)), only<Event.ImageZoom>())
    }

    @Test
    fun `zooming a note without images records nothing`() = runTest {
        attention.onOpened(visit(hasImages = false))

        attention.onZoomedIn()

        assertTrue(only<Event.ImageZoom>().isEmpty())
    }

    @Test
    fun `a note hidden behind another screen gathers no attention`() = runTest {
        attention.onOpened(visit())
        attention.onShown(false)
        advance(40.seconds)
        attention.onShown(true)

        attention.onClosed(isMarkedRead = false)

        assertEquals(Duration.ZERO, only<Event.NoteExposure>().single().activeTime)
    }
}
