package com.paulchibamba.margin.feature.read.book

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.paulchibamba.margin.domain.model.NoteId

@Composable
fun BookRoute(onBack: () -> Unit, onOpenNote: (NoteId) -> Unit, viewModel: BookViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val pickCover = rememberLauncherForActivityResult(PickVisualMedia()) { uri ->
        uri?.let { picked -> viewModel.onCoverPicked(picked.toString()) }
    }
    BookScreen(
        state = state,
        onBack = onBack,
        onOpenNote = onOpenNote,
        onMarkKnown = viewModel::onMarkKnown,
        onUndo = viewModel::onUndo,
        onUndoDismiss = viewModel::onUndoDismiss,
        onChooseCover = { pickCover.launch(PickVisualMediaRequest(PickVisualMedia.ImageOnly)) },
        onRemoveCover = viewModel::onRemoveCover,
    )
}
