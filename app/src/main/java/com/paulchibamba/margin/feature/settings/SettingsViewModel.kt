package com.paulchibamba.margin.feature.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.paulchibamba.margin.domain.memory.DesiredRetention
import com.paulchibamba.margin.domain.model.BookSettings
import com.paulchibamba.margin.domain.model.DarkMode
import com.paulchibamba.margin.domain.usecase.ObserveLearningSettings
import com.paulchibamba.margin.domain.usecase.SetDarkMode
import com.paulchibamba.margin.domain.usecase.SetDarkPosts
import com.paulchibamba.margin.domain.usecase.SetDesiredRetention
import com.paulchibamba.margin.domain.usecase.SetReviewReminder
import com.paulchibamba.margin.domain.usecase.UpdateBookSettings
import com.paulchibamba.margin.feature.reminder.ReminderScheduler
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

private const val STOP_TIMEOUT_MILLIS = 5_000L

@HiltViewModel
class SettingsViewModel @Inject constructor(
    observeLearningSettings: ObserveLearningSettings,
    private val updateBookSettings: UpdateBookSettings,
    private val setDesiredRetention: SetDesiredRetention,
    private val setReviewReminder: SetReviewReminder,
    private val setDarkMode: SetDarkMode,
    private val setDarkPosts: SetDarkPosts,
    private val reminderScheduler: ReminderScheduler,
) : ViewModel() {

    private val retentionDraft = MutableStateFlow<Double?>(null)

    val uiState: StateFlow<SettingsUiState> = combine(observeLearningSettings(), retentionDraft, SettingsUiState::of)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS), SettingsUiState())

    fun onBookSettingsChange(update: BookSettings) {
        viewModelScope.launch { updateBookSettings(update) }
    }

    fun onRetentionChange(retention: Double) {
        retentionDraft.value = DesiredRetention.normalise(retention)
    }

    fun onRetentionChangeFinished() {
        val retention = retentionDraft.value ?: return
        viewModelScope.launch { setDesiredRetention(retention) }
    }

    fun onReviewReminderChange(isOn: Boolean) {
        viewModelScope.launch {
            setReviewReminder(isOn)
            if (isOn) reminderScheduler.schedule() else reminderScheduler.cancel()
        }
    }

    fun onDarkModeChange(mode: DarkMode) {
        viewModelScope.launch { setDarkMode(mode) }
    }

    fun onDarkPostsChange(isOn: Boolean) {
        viewModelScope.launch { setDarkPosts(isOn) }
    }
}
