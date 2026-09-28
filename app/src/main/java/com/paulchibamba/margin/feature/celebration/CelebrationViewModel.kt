package com.paulchibamba.margin.feature.celebration

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.paulchibamba.margin.domain.usecase.ObserveStreak
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

private const val STOP_TIMEOUT_MILLIS = 5_000L

@HiltViewModel
class CelebrationViewModel @Inject constructor(
    private val queue: CelebrationQueue,
    observeStreak: ObserveStreak,
) : ViewModel() {

    val uiState: StateFlow<CelebrationUiState> = combine(queue.pending, observeStreak()) { pending, streak ->
        CelebrationUiState(pending.firstOrNull(), streak, isLoaded = true)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS), CelebrationUiState())

    fun onDone(celebration: Celebration) {
        queue.markShown(celebration)
    }
}
