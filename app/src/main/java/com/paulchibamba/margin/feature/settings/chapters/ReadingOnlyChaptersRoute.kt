package com.paulchibamba.margin.feature.settings.chapters

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun ReadingOnlyChaptersRoute(onBack: () -> Unit, viewModel: ReadingOnlyChaptersViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    ReadingOnlyChaptersScreen(state, onBack = onBack, onReadingOnlyChange = viewModel::onReadingOnlyChange)
}
