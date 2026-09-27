package com.paulchibamba.margin.feature.read.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.paulchibamba.margin.domain.usecase.ObserveReadingHome
import com.paulchibamba.margin.domain.usecase.ObserveStreak
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

private const val STOP_TIMEOUT_MILLIS = 5_000L

@HiltViewModel
class ReadHomeViewModel @Inject constructor(
    observeReadingHome: ObserveReadingHome,
    observeStreak: ObserveStreak,
) : ViewModel() {

    val uiState: StateFlow<ReadHomeUiState> = combine(observeReadingHome(), observeStreak()) { home, streak ->
        ReadHomeUiState.of(home, streak.currentStreak)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS), ReadHomeUiState())
}
