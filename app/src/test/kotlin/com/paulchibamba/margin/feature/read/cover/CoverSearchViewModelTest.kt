package com.paulchibamba.margin.feature.read.cover

import androidx.lifecycle.SavedStateHandle
import com.paulchibamba.margin.domain.model.BookSlug
import com.paulchibamba.margin.domain.usecase.FakeBookCoverRepository
import com.paulchibamba.margin.domain.usecase.FakeCoverImageStore
import com.paulchibamba.margin.domain.usecase.FixedClock
import com.paulchibamba.margin.domain.usecase.SetBookCover
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class CoverSearchViewModelTest {

    private val book = BookSlug("alice-bob-appsec")
    private val imageUrl = "https://external-content.duckduckgo.com/iu/?u=cover.jpg"
    private val covers = FakeBookCoverRepository()
    private val images = FakeCoverImageStore()

    @Before
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `the search opens on the book's title`() {
        val state = viewModel().uiState.value

        assertEquals("Alice and Bob Learn Application Security", state.bookTitle)
        assertEquals(CoverSearchUrl.of(state.bookTitle), state.searchUrl)
    }

    @Test
    fun `a long-pressed image waits for confirmation and cancelling drops it`() {
        val viewModel = viewModel()

        viewModel.onImageLongPressed(imageUrl)
        assertEquals(imageUrl, viewModel.uiState.value.pendingImageUrl)

        viewModel.onCancelImage()
        assertNull(viewModel.uiState.value.pendingImageUrl)
        assertTrue(covers.covers.value.isEmpty())
    }

    @Test
    fun `using the image sets the book's cover and closes the search`() {
        val viewModel = viewModel()
        viewModel.onImageLongPressed(imageUrl)

        viewModel.onUseImage()

        assertNotNull(covers.covers.value[book])
        assertTrue(viewModel.uiState.value.isDone)
        assertNull(viewModel.uiState.value.pendingImageUrl)
    }

    @Test
    fun `an image that can't be used says so and keeps the search open`() {
        images.canRead = false
        val viewModel = viewModel()
        viewModel.onImageLongPressed(imageUrl)

        viewModel.onUseImage()

        val state = viewModel.uiState.value
        assertTrue(state.isRejected)
        assertFalse(state.isDone)
        assertTrue(covers.covers.value.isEmpty())
        viewModel.onRejectionShown()
        assertFalse(viewModel.uiState.value.isRejected)
    }

    private fun viewModel() = CoverSearchViewModel(
        SavedStateHandle(mapOf("slug" to book.value, "title" to "Alice and Bob Learn Application Security")),
        SetBookCover(covers, images, FixedClock()),
    )
}
