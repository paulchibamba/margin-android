package com.paulchibamba.margin.feature.settings.screentime

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.paulchibamba.margin.domain.repository.UsageSource
import com.paulchibamba.margin.domain.usecase.DeleteScreenTime
import com.paulchibamba.margin.domain.usecase.IngestScreenTime
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

private const val TAG = "MarginScreenTime"

@HiltViewModel
class ScreenTimeSettingsViewModel @Inject constructor(
    private val usageSource: UsageSource,
    private val ingestScreenTime: IngestScreenTime,
    private val deleteScreenTime: DeleteScreenTime,
) : ViewModel() {

    private val state = MutableStateFlow(ScreenTimeSettingState())
    val uiState: StateFlow<ScreenTimeSettingState> = state.asStateFlow()

    fun onResume() {
        val hasAccess = usageSource.hasAccess()
        val isNewlyGranted = hasAccess && !state.value.hasAccess
        state.update { it.copy(hasAccess = hasAccess) }
        if (isNewlyGranted) viewModelScope.launch { ingest() }
    }

    fun onUseScreenTimeClick() {
        val dialog = if (state.value.hasAccess) ScreenTimeDialog.TURN_OFF else ScreenTimeDialog.TURN_ON
        state.update { it.copy(dialog = dialog) }
    }

    fun onDeleteClick() {
        state.update { it.copy(dialog = ScreenTimeDialog.CONFIRM_DELETE) }
    }

    fun onDialogDismissed() {
        state.update { it.copy(dialog = null) }
    }

    fun onDeleteConfirmed() {
        state.update { it.copy(dialog = null) }
        viewModelScope.launch {
            deleteScreenTime()
            state.update { it.copy(isDeleted = true) }
        }
    }

    private suspend fun ingest() {
        runCatching { ingestScreenTime() }.onFailure { error -> Log.w(TAG, "Couldn't read screen time", error) }
    }
}
