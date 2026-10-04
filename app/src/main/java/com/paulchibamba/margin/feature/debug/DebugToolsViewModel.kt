package com.paulchibamba.margin.feature.debug

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.paulchibamba.margin.domain.time.OffsetClock
import com.paulchibamba.margin.domain.usecase.CountDueReviews
import com.paulchibamba.margin.domain.usecase.GetRecentEvents
import com.paulchibamba.margin.domain.usecase.ResetProgress
import com.paulchibamba.margin.feature.reminder.ReminderScheduler
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlin.time.Duration
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class DebugToolsViewModel @Inject constructor(
    private val clock: OffsetClock,
    private val countDueReviews: CountDueReviews,
    private val reminderScheduler: ReminderScheduler,
    private val resetProgress: ResetProgress,
    private val getRecentEvents: GetRecentEvents,
) : ViewModel() {

    private val state = MutableStateFlow(DebugToolsUiState(offset = clock.offset))
    val uiState: StateFlow<DebugToolsUiState> = state.asStateFlow()

    fun onShown() = showOffset()

    fun onAdvance(by: Duration) {
        clock.advance(by)
        showOffset()
    }

    fun onResetClock() {
        clock.reset()
        showOffset()
    }

    fun onShowDueCount() {
        viewModelScope.launch {
            val count = countDueReviews()
            state.update { it.copy(dueCount = count) }
        }
    }

    fun onSendReviewReminder() = reminderScheduler.runOnce()

    fun onShowEvents() {
        viewModelScope.launch {
            val lines = getRecentEvents().map { event -> RecentEventLabels.lineFor(event, clock.zone()) }
            state.update { it.copy(recentEvents = lines) }
        }
    }

    fun onResetProgress() {
        if (!state.value.isProgressResetArmed) {
            state.update { it.copy(isProgressResetArmed = true) }
            return
        }
        viewModelScope.launch {
            resetProgress()
            state.update { it.copy(isProgressResetArmed = false, isProgressCleared = true) }
        }
    }

    private fun showOffset() {
        state.update { it.copy(offset = clock.offset, dueCount = null) }
    }
}
