package com.paulchibamba.margin.feature.settings.progressposts

import com.paulchibamba.margin.domain.llm.LlmSettings
import com.paulchibamba.margin.domain.llm.LlmSpend
import com.paulchibamba.margin.domain.usecase.LlmOverview

data class ProgressPostsUiState(
    val maskedKey: String? = null,
    val settings: LlmSettings = LlmSettings(),
    val spentToday: LlmSpend = LlmSpend.NONE,
    val connection: ConnectionCheck = ConnectionCheck.Idle,
    val keyDialog: KeyDialogState = KeyDialogState.CLOSED,
) {
    val isTemplatesOnly: Boolean get() = maskedKey == null
    val canTestConnection: Boolean get() = !isTemplatesOnly && connection != ConnectionCheck.Running

    companion object {
        fun of(overview: LlmOverview, connection: ConnectionCheck, keyDialog: KeyDialogState) = ProgressPostsUiState(
            maskedKey = overview.maskedKey,
            settings = overview.settings,
            spentToday = overview.spentToday,
            connection = connection,
            keyDialog = keyDialog,
        )
    }
}
