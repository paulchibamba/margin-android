package com.paulchibamba.margin.feature.settings.progressposts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.paulchibamba.margin.domain.llm.LlmModel
import com.paulchibamba.margin.domain.llm.LlmSettings
import com.paulchibamba.margin.domain.llm.MicroDollars
import com.paulchibamba.margin.domain.usecase.ObserveLlmOverview
import com.paulchibamba.margin.domain.usecase.RemoveApiKey
import com.paulchibamba.margin.domain.usecase.SaveApiKey
import com.paulchibamba.margin.domain.usecase.TestLlmConnection
import com.paulchibamba.margin.domain.usecase.UpdateLlmSettings
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
class ProgressPostsViewModel @Inject constructor(
    observeLlmOverview: ObserveLlmOverview,
    private val saveApiKey: SaveApiKey,
    private val removeApiKey: RemoveApiKey,
    private val updateLlmSettings: UpdateLlmSettings,
    private val testLlmConnection: TestLlmConnection,
) : ViewModel() {

    private val connection = MutableStateFlow<ConnectionCheck>(ConnectionCheck.Idle)
    private val keyDialog = MutableStateFlow(KeyDialogState.CLOSED)

    val uiState: StateFlow<ProgressPostsUiState> =
        combine(observeLlmOverview(), connection, keyDialog, ProgressPostsUiState::of)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS), ProgressPostsUiState())

    fun onKeyClick() {
        keyDialog.value = KeyDialogState.OPEN
    }

    fun onKeyDialogDismissed() {
        keyDialog.value = KeyDialogState.CLOSED
    }

    fun onKeySave(pasted: String) {
        viewModelScope.launch {
            if (saveApiKey(pasted)) closeKeyDialog() else keyDialog.value = KeyDialogState.NOT_A_KEY
        }
    }

    fun onKeyRemove() {
        viewModelScope.launch {
            removeApiKey()
            closeKeyDialog()
        }
    }

    fun onTestConnection() {
        if (connection.value == ConnectionCheck.Running) return
        connection.value = ConnectionCheck.Running
        viewModelScope.launch { connection.value = ConnectionCheck.Done(testLlmConnection()) }
    }

    fun onModelChange(model: LlmModel) = update { settings -> settings.copy(bakeModel = model) }

    fun onDailyCapChange(cap: MicroDollars) = update { settings -> settings.copy(dailyCap = cap) }

    fun onSendExcerptsChange(isOn: Boolean) = update { settings -> settings.copy(isSendingExcerpts = isOn) }

    private fun update(change: (LlmSettings) -> LlmSettings) {
        viewModelScope.launch { updateLlmSettings(change) }
    }

    private fun closeKeyDialog() {
        keyDialog.value = KeyDialogState.CLOSED
        connection.value = ConnectionCheck.Idle
    }
}
