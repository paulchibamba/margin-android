package com.paulchibamba.margin.feature.settings.progressposts

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun ProgressPostsSettings(viewModel: ProgressPostsViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    ProgressPostsCard(
        state,
        onKeyClick = viewModel::onKeyClick,
        onTestConnection = viewModel::onTestConnection,
        onModelChange = viewModel::onModelChange,
        onDailyCapChange = viewModel::onDailyCapChange,
        onSendExcerptsChange = viewModel::onSendExcerptsChange,
    )
    ApiKeyDialog(
        state.keyDialog,
        hasKey = !state.isTemplatesOnly,
        onSave = viewModel::onKeySave,
        onRemove = viewModel::onKeyRemove,
        onDismiss = viewModel::onKeyDialogDismissed,
    )
}
