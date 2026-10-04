package com.paulchibamba.margin.data.repository

import com.paulchibamba.margin.data.database.DatabaseTest
import com.paulchibamba.margin.data.database.MetaKey
import com.paulchibamba.margin.data.database.entity.MetaEntity
import com.paulchibamba.margin.data.pack.MapAssetSource
import com.paulchibamba.margin.data.pack.PackImporter
import com.paulchibamba.margin.data.pack.PackMapper
import com.paulchibamba.margin.data.pack.PackReader
import com.paulchibamba.margin.data.pack.RecordingLogger
import com.paulchibamba.margin.data.startup.StartupInitializer
import com.paulchibamba.margin.domain.model.BookSettings
import com.paulchibamba.margin.domain.model.BookSlug
import com.paulchibamba.margin.domain.model.DarkMode
import com.paulchibamba.margin.domain.model.Priority
import com.paulchibamba.margin.domain.tracking.Event
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class RoomSettingsRepositoryTest : DatabaseTest() {

    private val recorded = mutableListOf<Event>()
    private val repository by lazy { RoomSettingsRepository(database, emptyStartup(), recorded::add) }

    @Test
    fun `dark mode is off until it is set`() = runTest {
        assertEquals(DarkMode.OFF, repository.darkMode())
    }

    @Test
    fun `a saved dark mode is read back`() = runTest {
        repository.setDarkMode(DarkMode.FOLLOW_SYSTEM)
        assertEquals(DarkMode.FOLLOW_SYSTEM, repository.observeDarkMode().first())

        repository.setDarkMode(DarkMode.ALWAYS)
        assertEquals(DarkMode.ALWAYS, repository.darkMode())
    }

    @Test
    fun `dark posts are off until turned on, and read back once saved`() = runTest {
        assertFalse(repository.isDarkPostsOn())

        repository.setDarkPosts(true)

        assertTrue(repository.observeDarkPosts().first())
    }

    @Test
    fun `an unknown stored dark mode falls back to off`() = runTest {
        database.metaDao().put(listOf(MetaEntity(MetaKey.DARK_MODE, "SEPIA")))

        assertEquals(DarkMode.OFF, repository.darkMode())
    }

    @Test
    fun `changing a setting records the old and new values`() = runTest {
        repository.setDarkMode(DarkMode.FOLLOW_SYSTEM)
        repository.setDarkMode(DarkMode.ALWAYS)

        assertEquals<List<Event>>(
            listOf(
                Event.SettingChanged(MetaKey.DARK_MODE, old = null, new = "FOLLOW_SYSTEM"),
                Event.SettingChanged(MetaKey.DARK_MODE, old = "FOLLOW_SYSTEM", new = "ALWAYS"),
            ),
            recorded,
        )
    }

    @Test
    fun `saving a setting that did not change records nothing`() = runTest {
        repository.setDarkPosts(true)
        repository.setDarkPosts(true)

        assertEquals(1, recorded.size)
    }

    @Test
    fun `book and reading-only changes are recorded per book`() = runTest {
        repository.saveBookSettings(BookSettings(BookSlug("appsec"), isActive = true, priority = Priority.MAIN))
        repository.setReadingOnlyChapters(BookSlug("appsec"), setOf(3, 1))

        assertEquals(
            listOf("book_active:appsec" to "true", "book_priority:appsec" to "main", "reading_only:appsec" to "1,3"),
            recorded.filterIsInstance<Event.SettingChanged>().map { it.key to it.new },
        )
    }

    private fun emptyStartup() = StartupInitializer(
        PackImporter(PackReader(MapAssetSource(emptyMap())), PackMapper(), database, RecordingLogger()),
    )
}
