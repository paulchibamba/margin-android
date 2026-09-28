package com.paulchibamba.margin.feature.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.paulchibamba.margin.domain.memory.DesiredRetention
import com.paulchibamba.margin.domain.model.BookSettings
import com.paulchibamba.margin.domain.usecase.ObserveLearningSettings
import com.paulchibamba.margin.domain.usecase.SetDesiredRetention
import com.paulchibamba.margin.domain.usecase.UpdateBookSettings
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val STOP_TIMEOUT_MILLIS = 5_000L

@HiltViewModel
class SettingsViewModel @Inject constructor(
    observeLearningSettings: ObserveLearningSettings,
    private val updateBookSettings: UpdateBookSettings,
    private val setDesiredRetention: SetDesiredRetention,
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
}
