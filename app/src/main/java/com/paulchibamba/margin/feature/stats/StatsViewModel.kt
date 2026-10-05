package com.paulchibamba.margin.feature.stats

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.paulchibamba.margin.domain.stats.StatsTextFormatter
import com.paulchibamba.margin.domain.usecase.ObserveAttention
import com.paulchibamba.margin.domain.usecase.ObserveStats
import com.paulchibamba.margin.domain.usecase.RollUpEvents
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
private const val TAG = "MarginStats"

@HiltViewModel
class StatsViewModel @Inject constructor(
    observeStats: ObserveStats,
    observeAttention: ObserveAttention,
    private val rollUpEvents: RollUpEvents,
) : ViewModel() {

    private val isCopied = MutableStateFlow(false)
    private var copiedConfirmation: Job? = null

    init {
        viewModelScope.launch { rollUpToday() }
    }

    val uiState: StateFlow<StatsUiState> = combine(
        observeStats(),
        observeAttention(),
        isCopied,
    ) { report, attention, isCopied ->
        StatsUiState(report, attention, StatsTextFormatter.format(report, attention), isCopied)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS), StatsUiState())

    fun onCopied() {
        copiedConfirmation?.cancel()
        copiedConfirmation = viewModelScope.launch {
            isCopied.value = true
            delay(COPIED_CONFIRMATION)
            isCopied.value = false
        }
    }

    private suspend fun rollUpToday() {
        runCatching { rollUpEvents.throughToday() }.onFailure { error -> Log.w(TAG, "Couldn't roll up today", error) }
    }
}
