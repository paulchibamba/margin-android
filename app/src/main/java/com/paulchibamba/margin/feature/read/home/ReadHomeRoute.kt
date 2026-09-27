package com.paulchibamba.margin.feature.read.home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.paulchibamba.margin.domain.model.BookSlug
import com.paulchibamba.margin.domain.model.NoteId

@Composable
fun ReadHomeRoute(
    onOpenBook: (BookSlug) -> Unit,
    onOpenNote: (NoteId) -> Unit,
    onOpenSettings: () -> Unit,
    viewModel: ReadHomeViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    ReadHomeScreen(state, onOpenBook = onOpenBook, onOpenNote = onOpenNote, onOpenSettings = onOpenSettings)
}
