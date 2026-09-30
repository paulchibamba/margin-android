package com.paulchibamba.margin.domain.usecase

import com.paulchibamba.margin.domain.model.BookSlug
import com.paulchibamba.margin.domain.model.CoverSource
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import java.time.Duration
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class BookCoverUseCasesTest {

    private val book = BookSlug("alice-bob-appsec")
    private val picked = CoverSource.GalleryImage("content://media/picker/0/1")
    private val clock = FixedClock()
    private val covers = FakeBookCoverRepository()
    private val images = FakeCoverImageStore()
    private val setBookCover = SetBookCover(covers, images, clock)
    private val removeBookCover = RemoveBookCover(covers, images)
    private val observeBookCovers = ObserveBookCovers(covers)

    @Test
    fun `setting a cover saves the image and records it for the book`() = runTest {
        assertTrue(setBookCover(book, picked))

        val cover = observeBookCovers().first().getValue(book)
        assertEquals(clock.now(), cover.updatedAt)
        assertEquals(setOf(cover.imagePath), images.files)
    }

    @Test
    fun `replacing a cover deletes the old image`() = runTest {
        setBookCover(book, picked)
        val first = covers.cover(book)!!.imagePath
        clock.instant += Duration.ofMinutes(1)

        setBookCover(book, picked)

        val second = covers.cover(book)!!.imagePath
        assertEquals(setOf(second), images.files)
        assertFalse(first in images.files)
    }

    @Test
    fun `an unreadable image leaves the current cover in place`() = runTest {
        setBookCover(book, picked)
        val current = covers.cover(book)
        images.canRead = false

        assertFalse(setBookCover(book, picked))

        assertEquals(current, covers.cover(book))
        assertEquals(setOf(current!!.imagePath), images.files)
    }

    @Test
    fun `removing a cover forgets it and deletes its image`() = runTest {
        setBookCover(book, picked)

        removeBookCover(book)

        assertNull(observeBookCovers().first()[book])
        assertTrue(images.files.isEmpty())
    }

    @Test
    fun `removing a cover from a book without one changes nothing`() = runTest {
        removeBookCover(book)

        assertTrue(covers.covers.value.isEmpty())
    }
}
