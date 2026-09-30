package com.paulchibamba.margin.data.repository

import com.paulchibamba.margin.data.cover.CoverDirectory
import com.paulchibamba.margin.data.database.DatabaseTest
import com.paulchibamba.margin.domain.model.BookCover
import com.paulchibamba.margin.domain.model.BookSlug
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.io.File
import java.time.Instant
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

@RunWith(RobolectricTestRunner::class)
class RoomBookCoverRepositoryTest : DatabaseTest() {

    private val root = File("/data/margin/covers")
    private val repository by lazy { RoomBookCoverRepository(database, CoverDirectory(root)) }
    private val book = BookSlug("alice-bob-appsec")
    private val cover = BookCover(book, File(root, "alice-bob-appsec-6000.webp").path, Instant.ofEpochMilli(6_000))

    @Test
    fun `a saved cover is read back with its path in the covers folder`() = runTest {
        repository.saveCover(cover)

        assertEquals(cover, repository.cover(book))
        assertEquals(listOf(cover), repository.observeCovers().first())
    }

    @Test
    fun `only the file name is stored so the covers folder can move`() = runTest {
        repository.saveCover(cover)

        assertEquals("alice-bob-appsec-6000.webp", database.bookCoverDao().cover(book.value)?.fileName)
    }

    @Test
    fun `saving again replaces the book's cover`() = runTest {
        repository.saveCover(cover)
        val newerImage = File(root, "alice-bob-appsec-9000.webp").path
        val newer = cover.copy(imagePath = newerImage, updatedAt = Instant.ofEpochMilli(9_000))

        repository.saveCover(newer)

        assertEquals(listOf(newer), repository.observeCovers().first())
    }

    @Test
    fun `a removed cover is gone`() = runTest {
        repository.saveCover(cover)

        repository.removeCover(book)

        assertNull(repository.cover(book))
        assertTrue(repository.observeCovers().first().isEmpty())
    }
}
