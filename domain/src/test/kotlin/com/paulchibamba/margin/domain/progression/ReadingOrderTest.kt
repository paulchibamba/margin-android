package com.paulchibamba.margin.domain.progression

import com.paulchibamba.margin.domain.feed.aiSecurity
import com.paulchibamba.margin.domain.feed.appSec
import com.paulchibamba.margin.domain.feed.grokking
import com.paulchibamba.margin.domain.model.BookSettings
import com.paulchibamba.margin.domain.model.Priority
import kotlin.test.Test
import kotlin.test.assertEquals

class ReadingOrderTest {

    private val books = listOf(appSec.slug, grokking.slug, aiSecurity.slug)

    @Test
    fun `active books come first by priority and then in book order`() {
        val settings = listOf(
            BookSettings(appSec.slug, isActive = true, priority = Priority.NORMAL),
            BookSettings(grokking.slug, isActive = false, priority = Priority.MAIN),
            BookSettings(aiSecurity.slug, isActive = true, priority = Priority.MAIN),
        )

        assertEquals(listOf(aiSecurity.slug, appSec.slug, grokking.slug), ReadingOrder(books, settings).activeFirst())
    }

    @Test
    fun `with no active book every book is read next in book order`() {
        assertEquals(books, ReadingOrder(books, emptyList()).booksToReadNext())
    }
}
