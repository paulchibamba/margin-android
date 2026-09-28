package com.paulchibamba.margin.feature.settings.chapters

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.paulchibamba.margin.domain.model.ChapterRef
import com.paulchibamba.margin.domain.usecase.ObserveLearningSettings
import com.paulchibamba.margin.domain.usecase.SetChapterReadingOnly
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val STOP_TIMEOUT_MILLIS = 5_000L

@HiltViewModel
class ReadingOnlyChaptersViewModel @Inject constructor(
    observeLearningSettings: ObserveLearningSettings,
    private val setChapterReadingOnly: SetChapterReadingOnly,
) : ViewModel() {

    val uiState: StateFlow<ReadingOnlyChaptersUiState> = observeLearningSettings()
        .map { settings -> ReadingOnlyChaptersUiState(settings.books, isLoading = false) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS), ReadingOnlyChaptersUiState())

    fun onReadingOnlyChange(chapter: ChapterRef, isReadingOnly: Boolean) {
        viewModelScope.launch { setChapterReadingOnly(chapter, isReadingOnly) }
    }
}
