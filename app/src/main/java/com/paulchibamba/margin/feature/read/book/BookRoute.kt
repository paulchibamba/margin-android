package com.paulchibamba.margin.feature.read.book

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.paulchibamba.margin.domain.model.NoteId

@Composable
fun BookRoute(onBack: () -> Unit, onOpenNote: (NoteId) -> Unit, viewModel: BookViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    BookScreen(
        state = state,
        onBack = onBack,
        onOpenNote = onOpenNote,
        onMarkKnown = viewModel::onMarkKnown,
        onUndo = viewModel::onUndo,
        onUndoDismiss = viewModel::onUndoDismiss,
    )
}
