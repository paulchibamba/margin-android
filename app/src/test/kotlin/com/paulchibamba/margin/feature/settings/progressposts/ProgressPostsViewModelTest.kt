package com.paulchibamba.margin.feature.settings.progressposts

import com.paulchibamba.margin.domain.llm.ApiKey
import com.paulchibamba.margin.domain.llm.FakeApiKeyStore
import com.paulchibamba.margin.domain.llm.FakeLlmClient
import com.paulchibamba.margin.domain.llm.FakeLlmLedger
import com.paulchibamba.margin.domain.llm.FakeLlmSettingsRepository
import com.paulchibamba.margin.domain.llm.LlmModel
import com.paulchibamba.margin.domain.llm.LlmReply
import com.paulchibamba.margin.domain.llm.LlmSpend
import com.paulchibamba.margin.domain.llm.MicroDollars
import com.paulchibamba.margin.domain.usecase.CallLlm
import com.paulchibamba.margin.domain.usecase.FixedClock
import com.paulchibamba.margin.domain.usecase.ObserveLlmOverview
import com.paulchibamba.margin.domain.usecase.RemoveApiKey
import com.paulchibamba.margin.domain.usecase.SaveApiKey
import com.paulchibamba.margin.domain.usecase.TestLlmConnection
import com.paulchibamba.margin.domain.usecase.UpdateLlmSettings
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ProgressPostsViewModelTest {
    private val clock = FixedClock()
    private val keys = FakeApiKeyStore()
    private val settings = FakeLlmSettingsRepository()
    private val ledger = FakeLlmLedger()
    private val client = FakeLlmClient()
    private val viewModel by lazy {
        ProgressPostsViewModel(
            ObserveLlmOverview(keys, settings, ledger, clock),
            SaveApiKey(keys),
            RemoveApiKey(keys),
            UpdateLlmSettings(settings),
            TestLlmConnection(CallLlm(keys, settings, ledger, client, clock)),
        )
    }

    @Before
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `with no key the card is templates only and can't test the connection`() = runTest {
        collectState()

        assertTrue(viewModel.uiState.value.isTemplatesOnly)
        assertFalse(viewModel.uiState.value.canTestConnection)
    }

    @Test
    fun `pasting a key saves it, closes the dialog and shows it masked`() = runTest {
        collectState()
        viewModel.onKeyClick()

        viewModel.onKeySave(" sk-test-abcdWXYZ ")

        assertEquals(ApiKey("sk-test-abcdWXYZ"), keys.key)
        assertEquals(KeyDialogState.CLOSED, viewModel.uiState.value.keyDialog)
        assertEquals("••••WXYZ", viewModel.uiState.value.maskedKey)
    }

    @Test
    fun `pasting something that isn't a key keeps the dialog open`() = runTest {
        collectState()
        viewModel.onKeyClick()

        viewModel.onKeySave("not a key")

        assertEquals(KeyDialogState.NOT_A_KEY, viewModel.uiState.value.keyDialog)
        assertEquals(null, keys.key)
    }

    @Test
    fun `testing the connection makes one call and shows it in today's spend`() = runTest {
        collectState()
        viewModel.onKeySave("sk-test-abcdWXYZ")

        viewModel.onTestConnection()

        val state = viewModel.uiState.value
        assertEquals(ConnectionCheck.Done(LlmReply.Answered("""{"ok":true}""", LlmModel.GPT_5_NANO)), state.connection)
        assertEquals(1, client.requests.size)
        assertEquals(1, state.spentToday.calls)
    }

    @Test
    fun `removing the key goes back to templates only`() = runTest {
        collectState()
        viewModel.onKeySave("sk-test-abcdWXYZ")

        viewModel.onKeyRemove()

        assertTrue(viewModel.uiState.value.isTemplatesOnly)
    }

    @Test
    fun `model, cap and excerpts changes are saved`() = runTest {
        collectState()

        viewModel.onModelChange(LlmModel.GPT_5_MINI)
        viewModel.onDailyCapChange(MicroDollars(50_000))
        viewModel.onSendExcerptsChange(false)

        val saved = settings.settings()
        assertEquals(LlmModel.GPT_5_MINI, saved.bakeModel)
        assertEquals(MicroDollars(50_000), saved.dailyCap)
        assertFalse(saved.isSendingExcerpts)
        assertEquals(LlmSpend.NONE, viewModel.uiState.value.spentToday)
    }

    private fun TestScope.collectState() {
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.uiState.collect {} }
    }
}
