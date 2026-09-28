package com.paulchibamba.margin.feature.settings

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun SettingsRoute(
    onOpenReadingOnlyChapters: () -> Unit,
    onOpenStats: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    SettingsScreen(
        state = state,
        onBookSettingsChange = viewModel::onBookSettingsChange,
        onRetentionChange = viewModel::onRetentionChange,
        onRetentionChangeFinished = viewModel::onRetentionChangeFinished,
        onOpenReadingOnlyChapters = onOpenReadingOnlyChapters,
        onOpenStats = onOpenStats,
    )
}
