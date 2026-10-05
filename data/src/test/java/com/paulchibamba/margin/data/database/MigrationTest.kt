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

    @Test
    fun `moving from version 2 to 3 keeps progress and adds an empty event log`() {
        helper.createDatabase(NAME, 2).use { database ->
            database.execSQL("INSERT INTO note_read (noteId, readAt) VALUES ('$NOTE', 4000)")
            database.execSQL("INSERT INTO book_cover (bookSlug, fileName, updatedAt) VALUES ('appsec', 'a.webp', 5)")
        }

        helper.runMigrationsAndValidate(NAME, 3, true).use { database ->
            assertEquals(4000L, database.longOf("SELECT readAt FROM note_read WHERE noteId = '$NOTE'"))
            assertEquals(1L, database.longOf("SELECT COUNT(*) FROM book_cover"))
            assertEquals(0L, database.longOf("SELECT COUNT(*) FROM event_log"))
        }
    }

    @Test
    fun `moving from version 3 to 4 keeps the event log and adds an empty rollup table`() {
        helper.createDatabase(NAME, 3).use { database ->
            database.execSQL("INSERT INTO note_read (noteId, readAt) VALUES ('$NOTE', 4000)")
            database.execSQL(
                "INSERT INTO event_log (at, sessionId, type, subjectId, props, schemaVersion) " +
                    "VALUES (5, 's', 'session_start', NULL, '{}', 1)",
            )
        }

        helper.runMigrationsAndValidate(NAME, 4, true).use { database ->
            assertEquals(4000L, database.longOf("SELECT readAt FROM note_read WHERE noteId = '$NOTE'"))
            assertEquals(1L, database.longOf("SELECT COUNT(*) FROM event_log"))
            assertEquals(0L, database.longOf("SELECT COUNT(*) FROM daily_rollup"))
        }
    }

    @Test
    fun `moving from version 4 to 5 keeps the rollups and adds empty screen-time tables`() {
        helper.createDatabase(NAME, 4).use { database ->
            database.execSQL("INSERT INTO note_read (noteId, readAt) VALUES ('$NOTE', 4000)")
            database.execSQL(
                "INSERT INTO daily_rollup (date, metrics, computedAt, schemaVersion) VALUES ('2026-10-04', '{}', 5, 1)",
            )
        }

        helper.runMigrationsAndValidate(NAME, 5, true).use { database ->
            assertEquals(4000L, database.longOf("SELECT readAt FROM note_read WHERE noteId = '$NOTE'"))
            assertEquals(1L, database.longOf("SELECT COUNT(*) FROM daily_rollup"))
            assertEquals(0L, database.longOf("SELECT COUNT(*) FROM screen_time_daily"))
            assertEquals(0L, database.longOf("SELECT COUNT(*) FROM app_category_override"))
        }
    }

    @Test
    fun `moving from version 5 to 6 keeps progress and screen time and adds an empty generated-post table`() {
        helper.createDatabase(NAME, 5).use { database ->
            database.execSQL("INSERT INTO note_read (noteId, readAt) VALUES ('$NOTE', 4000)")
            database.execSQL("INSERT INTO meta (`key`, value) VALUES ('${MetaKey.DELIGHT_AT}', '17')")
            database.execSQL(
                "INSERT INTO screen_time_daily (date, packageName, label, category, isDoom, foregroundMs) " +
                    "VALUES ('2026-10-04', 'com.example', 'Example', 'social', 1, 60000)",
            )
        }

        helper.runMigrationsAndValidate(NAME, 6, true).use { database ->
            assertEquals(4000L, database.longOf("SELECT readAt FROM note_read WHERE noteId = '$NOTE'"))
            assertEquals(17L, database.longOf("SELECT value FROM meta WHERE `key` = '${MetaKey.DELIGHT_AT}'"))
            assertEquals(1L, database.longOf("SELECT COUNT(*) FROM screen_time_daily"))
            assertEquals(0L, database.longOf("SELECT COUNT(*) FROM generated_post"))
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
