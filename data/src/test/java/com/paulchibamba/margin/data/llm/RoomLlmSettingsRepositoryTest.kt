package com.paulchibamba.margin.data.llm

import com.paulchibamba.margin.data.database.DatabaseTest
import com.paulchibamba.margin.data.database.MetaKey
import com.paulchibamba.margin.domain.llm.LlmModel
import com.paulchibamba.margin.domain.llm.LlmSettings
import com.paulchibamba.margin.domain.llm.MicroDollars
import com.paulchibamba.margin.domain.tracking.Event
import kotlin.test.assertEquals
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class RoomLlmSettingsRepositoryTest : DatabaseTest() {

    private val recorded = mutableListOf<Event>()
    private val repository by lazy { RoomLlmSettingsRepository(database, recorded::add) }

    @Test
    fun `the defaults are gpt-5-nano, a two-cent cap and excerpts on`() = runTest {
        val settings = repository.settings()

        assertEquals(LlmModel.GPT_5_NANO, settings.bakeModel)
        assertEquals(MicroDollars(20_000), settings.dailyCap)
        assertEquals(true, settings.isSendingExcerpts)
    }

    @Test
    fun `saved settings are read back from meta`() = runTest {
        val saved = LlmSettings(LlmModel.GPT_5_MINI, MicroDollars(50_000), isSendingExcerpts = false)

        repository.saveSettings(saved)

        assertEquals(saved, repository.observeSettings().first())
        assertEquals("50000", database.metaDao().get(MetaKey.LLM_DAILY_CAP_MICROS))
        assertEquals("gpt-5-mini", database.metaDao().get(MetaKey.LLM_MODEL_BAKE))
        assertEquals("false", database.metaDao().get(MetaKey.SEND_EXCERPTS))
    }

    @Test
    fun `only changed settings are recorded as events`() = runTest {
        repository.saveSettings(LlmSettings())
        recorded.clear()

        repository.saveSettings(LlmSettings(dailyCap = MicroDollars(10_000)))

        val capChange = Event.SettingChanged(MetaKey.LLM_DAILY_CAP_MICROS, "20000", "10000")
        assertEquals<List<Event>>(listOf(capChange), recorded)
    }
}
