package com.paulchibamba.margin.feature.stats

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.paulchibamba.margin.domain.stats.StatsTextFormatter
import com.paulchibamba.margin.domain.usecase.ObserveStats
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds

private const val STOP_TIMEOUT_MILLIS = 5_000L
private val COPIED_CONFIRMATION = 2.seconds

@HiltViewModel
class StatsViewModel @Inject constructor(observeStats: ObserveStats) : ViewModel() {

    private val isCopied = MutableStateFlow(false)
    private var copiedConfirmation: Job? = null

    val uiState: StateFlow<StatsUiState> = combine(observeStats(), isCopied) { report, isCopied ->
        StatsUiState(report, StatsTextFormatter.format(report), isCopied)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS), StatsUiState())

    fun onCopied() {
        copiedConfirmation?.cancel()
        copiedConfirmation = viewModelScope.launch {
            isCopied.value = true
            delay(COPIED_CONFIRMATION)
            isCopied.value = false
        }
    }
}
