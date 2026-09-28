package com.paulchibamba.margin.feature.stats

import android.content.ClipData
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.ClipEntry
import androidx.compose.ui.platform.LocalClipboard
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.launch

private const val CLIP_LABEL = "Margin stats"

@Composable
fun StatsRoute(onBack: () -> Unit, viewModel: StatsViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val clipboard = LocalClipboard.current
    val scope = rememberCoroutineScope()
    StatsScreen(
        state = state,
        onBack = onBack,
        onCopy = {
            scope.launch {
                clipboard.setClipEntry(ClipEntry(ClipData.newPlainText(CLIP_LABEL, state.exportText)))
                viewModel.onCopied()
            }
        },
    )
}
