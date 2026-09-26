package com.paulchibamba.margin.data.database

import com.paulchibamba.margin.data.database.dao.FeedLogDao
import com.paulchibamba.margin.data.database.entity.ChapterKnownEntity
import com.paulchibamba.margin.data.database.entity.FeedHistoryEntity
import com.paulchibamba.margin.data.database.entity.NoteReadEntity
import com.paulchibamba.margin.data.database.entity.PostSeenEntity
import com.paulchibamba.margin.data.database.entity.SavedPostEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@RunWith(RobolectricTestRunner::class)
class ProgressDaoTest : DatabaseTest() {

    @Test
    fun `concept progress is upserted and read back`() = runTest {
        val dao = database.conceptProgressDao()
        dao.upsert(listOf(conceptProgress()))
        dao.upsert(listOf(conceptProgress().copy(confidence = null, lostGraded = false)))

        assertEquals(listOf(conceptProgress().copy(confidence = null, lostGraded = false)), dao.all())
        assertEquals(null, dao.byId("missing"))
    }

    @Test
    fun `the logs append actions and reviews in order`() = runTest {
        val dao = database.feedLogDao()
        dao.insertAction(actionLog())
        dao.insertAction(actionLog().copy(action = "save"))
        dao.insertReview(reviewLog())

        assertEquals(listOf("got", "save"), dao.actions().first().map { it.action })
        assertEquals(listOf(1L, 2L), dao.actions().first().map { it.id })
        assertEquals(reviewLog().copy(id = 1), dao.reviews().first().single())
    }

    @Test
    fun `the log DAO can only insert and read`() {
        val writes = FeedLogDao::class.java.declaredMethods.map { it.name }.filterNot { it.startsWith("insert") }

        assertEquals(setOf("actions", "reviews"), writes.toSet())
    }

    @Test
    fun `seen posts, affinity and saved posts are stored`() = runTest {
        val dao = database.feedStateDao()
        val seen = PostSeenEntity(POST, 1_000, 2_000, 5, 2, 5_000, 0.8, lastCorrect = null, lastSource = "new")
        dao.upsertSeen(listOf(seen))
        dao.upsertSeen(listOf(seen.copy(times = 3)))
        dao.insertSaved(SavedPostEntity(POST, savedAt = 1))
        dao.insertSaved(SavedPostEntity(POST, savedAt = 2))

        assertEquals(listOf(seen.copy(times = 3)), dao.seenPosts())
        assertEquals(listOf(SavedPostEntity(POST, savedAt = 1)), dao.savedPosts())
    }

    @Test
    fun `feed history keeps only the most recent steps`() = runTest {
        val dao = database.feedStateDao()
        dao.upsertHistory((1..40).map { step -> FeedHistoryEntity(step, POST, CONCEPT, "tip", "teach") })

        dao.trimHistory(keep = 30)

        assertEquals((11..40).toList(), dao.history().map { it.step })
    }

    @Test
    fun `reading keeps the first time a note was read and each known chapter once`() = runTest {
        val dao = database.readingDao()
        dao.markNoteRead(NoteReadEntity(NOTE, readAt = 1))
        dao.markNoteRead(NoteReadEntity(NOTE, readAt = 2))
        dao.markChapterKnown(ChapterKnownEntity(BOOK, chapter = 3, markedAt = 1))
        dao.markChapterKnown(ChapterKnownEntity(BOOK, chapter = 3, markedAt = 2))

        assertEquals(listOf(NoteReadEntity(NOTE, readAt = 1)), dao.notesRead().first())
        assertEquals(listOf(ChapterKnownEntity(BOOK, 3, markedAt = 1)), dao.chaptersKnown().first())
    }

    @Test
    fun `seeding book settings never overwrites existing ones`() = runTest {
        val dao = database.settingsDao()
        dao.upsertBookSettings(bookSettings(active = false, priority = "low"))

        val inserted = dao.insertBookSettingsIfAbsent(listOf(bookSettings(active = true, priority = "main")))

        assertEquals(listOf(-1L), inserted)
        assertEquals(listOf(bookSettings(active = false, priority = "low")), dao.bookSettings().first())
    }

    @Test
    fun `reading-only chapters are replaced per book`() = runTest {
        val dao = database.settingsDao()
        dao.replaceReadingOnlyChapters(BOOK, listOf(10, 11))
        dao.replaceReadingOnlyChapters("other-book", listOf(1))

        dao.replaceReadingOnlyChapters(BOOK, listOf(12))

        val chapters = dao.readingOnlyChapters().first().map { it.bookSlug to it.chapter }
        assertEquals(setOf(BOOK to 12, "other-book" to 1), chapters.toSet())
    }

    @Test
    fun `meta values are put, overwritten and found by prefix`() = runTest {
        val dao = database.metaDao()
        dao.put(listOf(meta(MetaKey.PACK_VERSION, "a"), meta(MetaKey.bookLastNew(BOOK), "4")))
        dao.put(listOf(meta(MetaKey.PACK_VERSION, "b")))

        assertEquals("b", dao.get(MetaKey.PACK_VERSION))
        assertEquals("b", dao.observe(MetaKey.PACK_VERSION).first())
        assertEquals(null, dao.get(MetaKey.LAST_NOTE))
        assertEquals(listOf("4"), dao.withPrefix("book_last_new:").map { it.value })
    }

    @Test
    fun `daily activity is upserted per date`() = runTest {
        val dao = database.activityDao()
        dao.upsert(activity("2026-10-02", postsSeen = 1))
        dao.upsert(activity("2026-10-01", postsSeen = 5))
        dao.upsert(activity("2026-10-02", postsSeen = 2))

        assertEquals(activity("2026-10-02", postsSeen = 2), dao.on("2026-10-02"))
        assertEquals(listOf("2026-10-01", "2026-10-02"), dao.all().first().map { it.date })
    }

    @Test
    fun `meta keys follow the documented shapes`() {
        assertEquals("book_last_new:$BOOK", MetaKey.bookLastNew(BOOK))
        assertEquals("badge_shown:$BOOK:introduced", MetaKey.badgeShown(BOOK, "introduced"))
        assertTrue(MetaKey.PACK_VERSION.isNotBlank())
    }
}
