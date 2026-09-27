package com.paulchibamba.margin.feature.read.book

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.paulchibamba.margin.domain.model.BookSlug
import com.paulchibamba.margin.domain.model.ChapterRef
import com.paulchibamba.margin.domain.usecase.MarkChapterKnown
import com.paulchibamba.margin.domain.usecase.ObserveBook
import com.paulchibamba.margin.domain.usecase.UnmarkChapterKnown
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val STOP_TIMEOUT_MILLIS = 5_000L
private const val BOOK_SLUG_KEY = "slug"

@HiltViewModel
class BookViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    observeBook: ObserveBook,
    private val markChapterKnown: MarkChapterKnown,
    private val unmarkChapterKnown: UnmarkChapterKnown,
) : ViewModel() {

    private val undoChapter = MutableStateFlow<ChapterRef?>(null)
    private val book = BookSlug(checkNotNull(savedStateHandle.get<String>(BOOK_SLUG_KEY)))

    val uiState: StateFlow<BookUiState> = combine(observeBook(book), undoChapter, BookUiState::of)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS), BookUiState())

    fun onMarkKnown(chapter: ChapterRef) {
        undoChapter.value = chapter
        viewModelScope.launch { markChapterKnown(chapter) }
    }

    fun onUndo() {
        val chapter = undoChapter.value ?: return
        undoChapter.value = null
        viewModelScope.launch { unmarkChapterKnown(chapter) }
    }

    fun onUndoDismiss() {
        undoChapter.value = null
    }
}
