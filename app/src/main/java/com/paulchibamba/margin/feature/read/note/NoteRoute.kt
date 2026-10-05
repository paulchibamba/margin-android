package com.paulchibamba.margin.feature.read.note

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun NoteRoute(onBack: () -> Unit, viewModel: NoteViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    LifecycleResumeEffect(viewModel) {
        viewModel.onShown(true)
        onPauseOrDispose { viewModel.onShown(false) }
    }
    NoteScreen(
        state = state,
        onBack = onBack,
        onPrevious = viewModel::onPrevious,
        onNext = {
            viewModel.onNext()
            if (state.next == null) onBack()
        },
        onScroll = viewModel::onScrolled,
        onZoomedIn = viewModel::onZoomedIn,
    )
}
