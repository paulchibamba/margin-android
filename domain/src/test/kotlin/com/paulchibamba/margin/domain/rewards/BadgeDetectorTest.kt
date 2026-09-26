package com.paulchibamba.margin.domain.rewards

import com.paulchibamba.margin.domain.model.BookSlug
import kotlin.test.Test
import kotlin.test.assertEquals

class BadgeDetectorTest {

    private val book = BookSlug("alice-bob-appsec")
    private val detector = BadgeDetector()

    @Test
    fun `a fully introduced book earns the Introduced badge`() {
        val completion = BookCompletion(book, introduced = 48, remembered = 12, total = 48)

        assertEquals(listOf(Badge(book, BadgeKind.INTRODUCED)), detector.newlyEarned(completion, emptySet()))
    }

    @Test
    fun `a fully remembered book earns both badges`() {
        val completion = BookCompletion(book, introduced = 48, remembered = 48, total = 48)

        assertEquals(
            listOf(Badge(book, BadgeKind.INTRODUCED), Badge(book, BadgeKind.REMEMBERED)),
            detector.newlyEarned(completion, emptySet()),
        )
    }

    @Test
    fun `a badge already shown is not reported again`() {
        val completion = BookCompletion(book, introduced = 48, remembered = 12, total = 48)
        val shown = detector.newlyEarned(completion, emptySet()).toSet()

        assertEquals(emptyList(), detector.newlyEarned(completion, shown))
    }

    @Test
    fun `a book with nothing to learn earns nothing`() {
        assertEquals(emptyList(), detector.newlyEarned(BookCompletion(book, 0, 0, 0), emptySet()))
    }
}
