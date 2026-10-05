package com.paulchibamba.margin.feature.debug

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun DebugToolsRoute(
    onClockChanged: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: DebugToolsViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    LaunchedEffect(viewModel) { viewModel.onShown() }
    LaunchedEffect(state.isProgressCleared) { if (state.isProgressCleared) AppRestart.restart(context) }
    DebugToolsSection(
        state = state,
        onAdvance = { duration ->
            viewModel.onAdvance(duration)
            onClockChanged()
        },
        onResetClock = {
            viewModel.onResetClock()
            onClockChanged()
        },
        onShowDueCount = viewModel::onShowDueCount,
        onSendReviewReminder = viewModel::onSendReviewReminder,
        onShowEvents = viewModel::onShowEvents,
        onBakeProgressPosts = viewModel::onBakeProgressPosts,
        onResetProgress = viewModel::onResetProgress,
        modifier = modifier,
    )
}
