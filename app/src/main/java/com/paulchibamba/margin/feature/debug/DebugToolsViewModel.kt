package com.paulchibamba.margin.feature.debug

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.paulchibamba.margin.domain.time.OffsetClock
import com.paulchibamba.margin.domain.usecase.CountDueReviews
import com.paulchibamba.margin.feature.reminder.ReminderScheduler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Duration

@HiltViewModel
class DebugClockViewModel @Inject constructor(
    private val clock: OffsetClock,
    private val countDueReviews: CountDueReviews,
    private val reminderScheduler: ReminderScheduler,
) : ViewModel() {

    private val state = MutableStateFlow(DebugClockUiState(offset = clock.offset))
    val uiState: StateFlow<DebugClockUiState> = state.asStateFlow()

    fun onShown() = showOffset()

    fun onAdvance(by: Duration) {
        clock.advance(by)
        showOffset()
    }

    fun onReset() {
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

    private fun showOffset() {
        state.update { it.copy(offset = clock.offset, dueCount = null) }
    }
}
