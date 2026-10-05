package com.paulchibamba.margin.feature.read.note

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import com.paulchibamba.margin.designsystem.LocalSurfacePalette
import com.paulchibamba.margin.designsystem.MarginTheme
import com.paulchibamba.margin.designsystem.StatusBarFollowsSkin
import com.paulchibamba.margin.designsystem.SurfacePalette
import com.paulchibamba.margin.designsystem.SurfacePaletteProvider
import com.paulchibamba.margin.designsystem.SurfacePreview
import com.paulchibamba.margin.domain.tracking.ScrollPosition

@Composable
fun NoteScreen(
    state: NoteUiState,
    onBack: () -> Unit,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    modifier: Modifier = Modifier,
    onScroll: (ScrollPosition) -> Unit = {},
    onZoomedIn: () -> Unit = {},
    body: @Composable (html: String, modifier: Modifier) -> Unit = { html, bodyModifier ->
        NoteWebView(html, bodyModifier, onScroll, onZoomedIn)
    },
) {
    val palette = LocalSurfacePalette.current
    MarginTheme(palette.skin) {
        StatusBarFollowsSkin()
        Column(modifier.fillMaxSize().background(palette.card).statusBarsPadding()) {
            NoteTopBar(state, onBack)
            if (!state.isLoading) {
                body(NoteHtml.documentOf(state.bodyHtml, palette), Modifier.weight(1f).fillMaxWidth())
            }
            if (!state.isLoading) NoteBottomBar(state, onPrevious = onPrevious, onNext = onNext)
        }
    }
}

@Preview(widthDp = 360, heightDp = 780)
@Composable
private fun NoteScreenFromPostPreview(@PreviewParameter(SurfacePaletteProvider::class) palette: SurfacePalette) {
    SurfacePreview(palette) {
        NoteScreen(NotePreviewData.fromPost, onBack = {}, onPrevious = {}, onNext = {}, body = { _, bodyModifier ->
            NotePreviewData.Body(bodyModifier)
        })
    }
}

@Preview(widthDp = 360, heightDp = 780, fontScale = 2f)
@Composable
private fun NoteScreenLargeTextPreview() {
    NoteScreen(NotePreviewData.fromPost, onBack = {}, onPrevious = {}, onNext = {}, body = { _, bodyModifier ->
        NotePreviewData.Body(bodyModifier)
    })
}

@Preview(widthDp = 360, heightDp = 780)
@Composable
private fun NoteScreenFromBookPreview() {
    NoteScreen(NotePreviewData.fromBook, onBack = {}, onPrevious = {}, onNext = {}, body = { _, bodyModifier ->
        NotePreviewData.Body(bodyModifier)
    })
}
