package com.paulchibamba.margin.data.database

import androidx.room.testing.MigrationTestHelper
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import kotlin.test.assertEquals

@RunWith(RobolectricTestRunner::class)
class MigrationTest {

    @get:Rule
    val helper = MigrationTestHelper(InstrumentationRegistry.getInstrumentation(), MarginDatabase::class.java)

    @Test
    fun `moving from version 1 to 2 keeps progress and adds an empty cover table`() {
        helper.createDatabase(NAME, 1).use { database ->
            database.execSQL("INSERT INTO note_read (noteId, readAt) VALUES ('$NOTE', 4000)")
            database.execSQL("INSERT INTO meta (`key`, value) VALUES ('${MetaKey.FEED_STEP}', '12')")
        }

        helper.runMigrationsAndValidate(NAME, 2, true).use { database ->
            assertEquals(4000L, database.longOf("SELECT readAt FROM note_read WHERE noteId = '$NOTE'"))
            assertEquals(12L, database.longOf("SELECT value FROM meta WHERE `key` = '${MetaKey.FEED_STEP}'"))
            assertEquals(0L, database.longOf("SELECT COUNT(*) FROM book_cover"))
        }
    }

    private fun SupportSQLiteDatabase.longOf(sql: String): Long =
        query(sql).use { cursor ->
            cursor.moveToFirst()
            cursor.getLong(0)
        }

    private companion object {
        const val NAME = "migration-test.db"
    }
}
