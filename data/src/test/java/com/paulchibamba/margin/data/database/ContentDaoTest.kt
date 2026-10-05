package com.paulchibamba.margin.data.database

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@RunWith(RobolectricTestRunner::class)
class ContentDaoTest : DatabaseTest() {

    private val content get() = database.contentDao()

    @Test
    fun `replaceAll stores every content table`() = runTest {
        val pack = packContent()

        content.replaceAll(pack)

        assertEquals(pack.books, content.books())
        assertEquals(pack.chapters, content.chapters())
        assertEquals(pack.concepts, content.concepts())
        assertEquals(pack.posts, content.posts())
        assertEquals(pack.notes, content.notesOf(BOOK))
        assertEquals(pack.notes.single(), content.note(NOTE))
    }

    @Test
    fun `replaceAll replaces the previous content`() = runTest {
        content.replaceAll(packContent())
        val next = packContent(title = "Second edition").copy(concepts = listOf(conceptEntity(id = "$BOOK/ch01/new")))

        content.replaceAll(next)

        assertEquals(listOf("Second edition"), content.books().map { it.title })
        assertEquals(listOf("$BOOK/ch01/new"), content.concepts().map { it.id })
    }

    @Test
    fun `replaceAll leaves every progress table untouched`() = runTest {
        database.fillProgressTables()
        val before = progressSnapshot()
        assertTrue(before.none { rows -> rows is List<*> && rows.isEmpty() })

        content.replaceAll(packContent(title = "Second edition"))

        assertEquals(before, progressSnapshot())
    }

    @Test
    fun `concepts come back in book order and notes in reading order`() = runTest {
        val shuffled = packContent().copy(
            concepts = listOf(conceptEntity("c3", 2, 0), conceptEntity("c2", 1, 1), conceptEntity("c1", 1, 0)),
            notes = listOf(noteEntity("n3", 2, 0), noteEntity("n1", 1, 0), noteEntity("n2", 1, 5)),
        )

        content.replaceAll(shuffled)

        assertEquals(listOf("c1", "c2", "c3"), content.concepts().map { it.id })
        assertEquals(listOf("n1", "n2", "n3"), content.notesOf(BOOK).map { it.id })
    }

    private suspend fun progressSnapshot(): List<Any?> = listOf(
        database.conceptProgressDao().all(),
        database.feedLogDao().actions().first(),
        database.feedLogDao().reviews().first(),
        database.feedStateDao().seenPosts(),
        database.feedStateDao().affinity(),
        database.feedStateDao().history(),
        database.feedStateDao().savedPosts(),
        database.readingDao().notesRead().first(),
        database.readingDao().chaptersKnown().first(),
        database.settingsDao().bookSettings().first(),
        database.settingsDao().readingOnlyChapters().first(),
        database.metaDao().get(MetaKey.FEED_STEP),
        database.activityDao().all().first(),
        database.generatedPostDao().all(),
    )
}
