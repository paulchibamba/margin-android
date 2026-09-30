package com.paulchibamba.margin.feature.read.cover

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.paulchibamba.margin.domain.model.BookSlug
import com.paulchibamba.margin.domain.model.CoverSource
import com.paulchibamba.margin.domain.usecase.SetBookCover
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val BOOK_SLUG_KEY = "slug"
private const val BOOK_TITLE_KEY = "title"

@HiltViewModel
class CoverSearchViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val setBookCover: SetBookCover,
) : ViewModel() {

    private val book = BookSlug(checkNotNull(savedStateHandle.get<String>(BOOK_SLUG_KEY)))
    private val state = MutableStateFlow(stateFor(savedStateHandle.get<String>(BOOK_TITLE_KEY).orEmpty()))
    val uiState: StateFlow<CoverSearchUiState> = state.asStateFlow()

    fun onImageLongPressed(url: String) {
        if (state.value.isSaving) return
        state.update { it.copy(pendingImageUrl = url, isRejected = false) }
    }

    fun onCancelImage() {
        state.update { it.copy(pendingImageUrl = null) }
    }

    fun onUseImage() {
        val url = state.value.pendingImageUrl ?: return
        state.update { it.copy(isSaving = true) }
        viewModelScope.launch {
            val isSet = setBookCover(book, CoverSource.WebImage(url))
            state.update { it.copy(pendingImageUrl = null, isSaving = false, isDone = isSet, isRejected = !isSet) }
        }
    }

    fun onRejectionShown() {
        state.update { it.copy(isRejected = false) }
    }

    private fun stateFor(bookTitle: String) = CoverSearchUiState(bookTitle, CoverSearchUrl.of(bookTitle))
}
