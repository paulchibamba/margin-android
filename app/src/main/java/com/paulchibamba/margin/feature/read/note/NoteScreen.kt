package com.paulchibamba.margin.feature.read.note

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.paulchibamba.margin.designsystem.MarginColors
import com.paulchibamba.margin.designsystem.MarginTheme
import com.paulchibamba.margin.designsystem.Skins
import com.paulchibamba.margin.designsystem.StatusBarFollowsSkin

@Composable
fun NoteScreen(
    state: NoteUiState,
    onBack: () -> Unit,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    modifier: Modifier = Modifier,
    body: @Composable (html: String, modifier: Modifier) -> Unit = { html, bodyModifier ->
        NoteWebView(html, bodyModifier)
    },
) {
    MarginTheme(Skins.Paper) {
        StatusBarFollowsSkin()
        Column(modifier.fillMaxSize().background(MarginColors.PaperCard).statusBarsPadding()) {
            NoteTopBar(state, onBack)
            if (!state.isLoading) body(state.html, Modifier.weight(1f).fillMaxWidth())
            if (!state.isLoading) NoteBottomBar(state, onPrevious = onPrevious, onNext = onNext)
        }
    }
}

@Preview(widthDp = 360, heightDp = 780)
@Composable
private fun NoteScreenFromPostPreview() {
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
