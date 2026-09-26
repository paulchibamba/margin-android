package com.paulchibamba.margin.domain.progression

import com.paulchibamba.margin.domain.model.BookSettings
import com.paulchibamba.margin.domain.model.BookSlug
import com.paulchibamba.margin.domain.model.Priority
import kotlin.test.Test
import kotlin.test.assertEquals

class PriorityShareTest {

    private val secureCoding = BookSlug("alice-bob-secure-coding")
    private val aiSecurity = BookSlug("practical-ai-security")
    private val priorityShare = PriorityShare()

    private val settings = listOf(
        BookSettings(appSec, isActive = true, priority = Priority.MAIN),
        BookSettings(grokking, isActive = true, priority = Priority.NORMAL),
        BookSettings(secureCoding, isActive = true, priority = Priority.NORMAL),
        BookSettings(aiSecurity, isActive = false, priority = Priority.LOW),
    )

    @Test
    fun `main and two normal books share new concepts sixty, twenty, twenty`() {
        assertEquals(0.6, priorityShare.targetShare(appSec, settings), TOLERANCE)
        assertEquals(0.2, priorityShare.targetShare(grokking, settings), TOLERANCE)
        assertEquals(0.2, priorityShare.targetShare(secureCoding, settings), TOLERANCE)
    }

    @Test
    fun `shares are normalised over the active books only`() {
        val twoBooks = settings.take(2)

        assertEquals(0.75, priorityShare.targetShare(appSec, twoBooks), TOLERANCE)
        assertEquals(0.25, priorityShare.targetShare(grokking, twoBooks), TOLERANCE)
    }

    @Test
    fun `an inactive book has no target share`() {
        assertEquals(0.0, priorityShare.targetShare(aiSecurity, settings))
    }

    @Test
    fun `before anything is introduced every book's actual share is zero`() {
        assertEquals(0.0, priorityShare.actualShare(appSec, introducedCounts = emptyMap(), settings))
    }

    @Test
    fun `the actual share counts introduced concepts across active books only`() {
        val introduced = mapOf(appSec to 6, grokking to 2, secureCoding to 2, aiSecurity to 10)

        assertEquals(0.6, priorityShare.actualShare(appSec, introduced, settings), TOLERANCE)
    }

    private companion object {
        const val TOLERANCE = 1e-9
    }
}
