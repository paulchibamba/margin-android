package com.paulchibamba.margin.domain.usecase

import com.paulchibamba.margin.domain.llm.ApiKey
import com.paulchibamba.margin.domain.llm.FakeApiKeyStore
import com.paulchibamba.margin.domain.llm.FakeLlmLedger
import com.paulchibamba.margin.domain.llm.FakeLlmSettingsRepository
import com.paulchibamba.margin.domain.llm.LlmSpend
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest

class LlmSettingsUseCasesTest {

    private val keys = FakeApiKeyStore()
    private val observe = ObserveLlmOverview(keys, FakeLlmSettingsRepository(), FakeLlmLedger(), FixedClock())

    @Test
    fun `with no key the overview is templates only`() = runTest {
        val overview = observe().first()

        assertTrue(overview.isTemplatesOnly)
        assertNull(overview.maskedKey)
        assertEquals(LlmSpend.NONE, overview.spentToday)
    }

    @Test
    fun `a saved key shows masked and turns templates only off`() = runTest {
        assertTrue(SaveApiKey(keys)(" sk-test-abcd1234 "))

        val overview = observe().first()

        assertEquals(ApiKey("sk-test-abcd1234"), keys.key)
        assertEquals("••••1234", overview.maskedKey)
        assertFalse(overview.isTemplatesOnly)
    }

    @Test
    fun `blank text is not saved as a key`() = runTest {
        assertFalse(SaveApiKey(keys)("  "))
        assertNull(keys.key)
    }

    @Test
    fun `removing the key goes back to templates only`() = runTest {
        SaveApiKey(keys)("sk-test-abcd1234")

        RemoveApiKey(keys)()

        assertTrue(observe().first().isTemplatesOnly)
    }
}
