package com.paulchibamba.margin.feature.debug

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun DebugToolsRoute(
    onClockChanged: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: DebugClockViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    LaunchedEffect(viewModel) { viewModel.onShown() }
    DebugToolsSection(
        state = state,
        onAdvance = { duration ->
            viewModel.onAdvance(duration)
            onClockChanged()
        },
        onReset = {
            viewModel.onReset()
            onClockChanged()
        },
        onShowDueCount = viewModel::onShowDueCount,
        modifier = modifier,
    )
}
