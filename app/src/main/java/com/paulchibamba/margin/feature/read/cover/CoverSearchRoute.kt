package com.paulchibamba.margin.feature.read.cover

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun CoverSearchRoute(onClose: () -> Unit, viewModel: CoverSearchViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    LaunchedEffect(state.isDone) { if (state.isDone) onClose() }
    CoverSearchScreen(
        state = state,
        onClose = onClose,
        onUseImage = viewModel::onUseImage,
        onCancelImage = viewModel::onCancelImage,
        onRejectionShown = viewModel::onRejectionShown,
        browser = { onProgress, modifier ->
            CoverSearchWebView(state.searchUrl, onProgress, viewModel::onImageLongPressed, modifier)
        },
    )
}
