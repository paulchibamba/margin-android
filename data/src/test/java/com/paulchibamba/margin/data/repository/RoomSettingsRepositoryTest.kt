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
import com.paulchibamba.margin.domain.model.DarkMode
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import kotlin.test.assertEquals

@RunWith(RobolectricTestRunner::class)
class RoomSettingsRepositoryTest : DatabaseTest() {

    private val repository by lazy { RoomSettingsRepository(database, emptyStartup()) }

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
    fun `an unknown stored dark mode falls back to off`() = runTest {
        database.metaDao().put(listOf(MetaEntity(MetaKey.DARK_MODE, "SEPIA")))

        assertEquals(DarkMode.OFF, repository.darkMode())
    }

    private fun emptyStartup() = StartupInitializer(
        PackImporter(PackReader(MapAssetSource(emptyMap())), PackMapper(), database, RecordingLogger()),
    )
}
