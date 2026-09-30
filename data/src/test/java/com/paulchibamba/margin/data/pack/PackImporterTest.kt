package com.paulchibamba.margin.data.pack

import com.paulchibamba.margin.data.database.DatabaseTest
import com.paulchibamba.margin.data.database.MetaKey
import com.paulchibamba.margin.data.database.entity.BookSettingsEntity
import com.paulchibamba.margin.data.database.fillProgressTables
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import kotlin.test.assertEquals
import kotlin.test.assertIs

@RunWith(RobolectricTestRunner::class)
class PackImporterTest : DatabaseTest() {

    private val logger = RecordingLogger()

    private fun importerFor(pack: PackFiles) = PackImporter(PackReader(pack.asAssets()), PackMapper(), database, logger)

    private suspend fun import(pack: PackFiles) = importerFor(pack).importIfChanged()

    @Test
    fun `a fresh database imports the pack and records its version`() = runTest {
        val result = import(packFiles(version = "v1"))

        assertEquals(ImportResult.Imported("v1", PackWarnings()), result)
        assertEquals("v1", database.metaDao().get(MetaKey.PACK_VERSION))
        assertEquals(4, database.contentDao().concepts().size)
        assertEquals(8, database.contentDao().posts().size)
    }

    @Test
    fun `the same version is skipped`() = runTest {
        import(packFiles(version = "v1"))

        assertEquals(ImportResult.Skipped("v1"), import(packFiles(version = "v1")))
    }

    @Test
    fun `re-importing a changed pack keeps every progress row`() = runTest {
        import(packFiles(version = "v1"))
        database.fillProgressTables()
        val before = progressRowCounts()

        val result = import(packFiles(version = "v2", books = listOf(bookFile(APPSEC, title = "Second edition"))))

        assertIs<ImportResult.Imported>(result)
        assertEquals(before, progressRowCounts())
        assertEquals(listOf("Second edition"), database.contentDao().books().map { it.title })
    }

    @Test
    fun `a failure mid-import leaves the old content and the old version`() = runTest {
        import(packFiles(version = "v1"))
        val oldConcepts = database.contentDao().concepts()
        val broken = bookFile(APPSEC).let { book -> book.copy(notes = book.notes + book.notes.first()) }

        val result = import(packFiles(version = "v2", books = listOf(broken)))

        assertIs<ImportResult.Failed>(result)
        assertEquals("v1", database.metaDao().get(MetaKey.PACK_VERSION))
        assertEquals(oldConcepts, database.contentDao().concepts())
    }

    @Test
    fun `an unreadable pack fails without touching the database`() = runTest {
        import(packFiles(version = "v1"))
        val bookFileMissing = packFiles(version = "v2").asAssets().let { assets ->
            AssetSource { path -> if (path == "pack/$APPSEC.json") error("missing $path") else assets.readText(path) }
        }
        val importer = PackImporter(PackReader(bookFileMissing), PackMapper(), database, logger)

        assertIs<ImportResult.Failed>(importer.importIfChanged())
        assertEquals("v1", database.metaDao().get(MetaKey.PACK_VERSION))
    }

    @Test
    fun `existing book settings are not overwritten by the library defaults`() = runTest {
        database.settingsDao().upsertBookSettings(BookSettingsEntity(APPSEC, active = false, priority = "low"))
        database.settingsDao().replaceReadingOnlyChapters(APPSEC, listOf(5))

        import(packFiles(version = "v1"))

        val settings = database.settingsDao().bookSettings().first().associateBy { it.bookSlug }
        assertEquals(BookSettingsEntity(APPSEC, active = false, priority = "low"), settings[APPSEC])
        assertEquals(BookSettingsEntity(GROKKING, active = false, priority = "normal"), settings[GROKKING])
        val readingOnly = database.settingsDao().readingOnlyChapters().first().map { it.bookSlug to it.chapter }
        assertEquals(setOf(APPSEC to 5, GROKKING to 1), readingOnly.toSet())
    }

    private suspend fun progressRowCounts(): List<Int> = listOf(
        database.conceptProgressDao().all().size,
        database.feedLogDao().actions().first().size,
        database.feedLogDao().reviews().first().size,
        database.feedStateDao().seenPosts().size,
        database.feedStateDao().affinity().size,
        database.feedStateDao().history().size,
        database.feedStateDao().savedPosts().size,
        database.readingDao().notesRead().first().size,
        database.readingDao().chaptersKnown().first().size,
        database.settingsDao().bookSettings().first().size,
        database.settingsDao().readingOnlyChapters().first().size,
        database.activityDao().all().first().size,
        database.bookCoverDao().covers().first().size,
    )
}
