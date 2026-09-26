package com.paulchibamba.margin.data.pack

import com.paulchibamba.margin.data.database.DatabaseTest
import com.paulchibamba.margin.data.database.MetaKey
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assume.assumeTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue

@RunWith(RobolectricTestRunner::class)
class RealPackImportTest : DatabaseTest() {

    @Test
    fun `a fresh database imports the real content pack`() = runTest {
        val assets = FileAssetSource.ofRealPack()
        assumeTrue("No content pack in content/pack: run scripts/sync-content-pack.sh", assets != null)
        val reader = PackReader(assets!!)
        val manifest = reader.readManifest()

        val result = PackImporter(reader, PackMapper(), database, RecordingLogger()).importIfChanged()

        assertIs<ImportResult.Imported>(result)
        assertEquals(manifest.version, database.metaDao().get(MetaKey.PACK_VERSION))
        assertEquals(manifest.books.map { it.slug }, database.contentDao().books().map { it.slug })
        assertEquals(manifest.books.size, database.settingsDao().bookSettings().first().size)
        assertTrue(database.contentDao().concepts().all { it.noteId == null || it.noteChapter != null })
        assertTrue(database.contentDao().posts().isNotEmpty())
    }
}
