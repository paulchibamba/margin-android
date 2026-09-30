package com.paulchibamba.margin.feature.read.cover

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.paulchibamba.margin.designsystem.MarginColors
import com.paulchibamba.margin.designsystem.MarginTheme
import com.paulchibamba.margin.designsystem.Skins
import com.paulchibamba.margin.designsystem.StatusBarFollowsSkin
import com.paulchibamba.margin.designsystem.component.FeedSnackbar
import kotlinx.coroutines.delay

private const val REJECTION_MILLIS = 6_000L

@Composable
fun CoverSearchScreen(
    state: CoverSearchUiState,
    onClose: () -> Unit,
    onUseImage: () -> Unit,
    onCancelImage: () -> Unit,
    onRejectionShown: () -> Unit,
    browser: @Composable (onProgress: (Int) -> Unit, modifier: Modifier) -> Unit,
    modifier: Modifier = Modifier,
) {
    var loadProgress by remember { mutableIntStateOf(0) }
    MarginTheme(Skins.Paper) {
        StatusBarFollowsSkin()
        Box(modifier.fillMaxSize().background(Skins.Paper.background).statusBarsPadding().navigationBarsPadding()) {
            Column(Modifier.fillMaxSize()) {
                CoverSearchTopBar(state.bookTitle, loadProgress, onClose)
                browser({ progress -> loadProgress = progress }, Modifier.weight(1f).fillMaxWidth())
            }
            if (state.isRejected) RejectionSnackbar(onRejectionShown)
        }
        if (state.pendingImageUrl != null) UseCoverDialog(state.bookTitle, state.isSaving, onUseImage, onCancelImage)
    }
}

@Composable
private fun RejectionSnackbar(onDismiss: () -> Unit) {
    LaunchedEffect(Unit) {
        delay(REJECTION_MILLIS)
        onDismiss()
    }
    Box(Modifier.fillMaxSize().padding(16.dp), contentAlignment = Alignment.BottomCenter) {
        FeedSnackbar(
            message = "Couldn't use that image",
            detail = "Try another one, or tap it first for a larger version",
            actionLabel = "OK",
            onAction = onDismiss,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
private fun CoverSearchPreviewOf(state: CoverSearchUiState) {
    CoverSearchScreen(
        state = state,
        onClose = {},
        onUseImage = {},
        onCancelImage = {},
        onRejectionShown = {},
        browser = { _, modifier -> Box(modifier.background(MarginColors.PaperCard)) },
    )
}

private val previewState = CoverSearchUiState(
    bookTitle = "Alice and Bob Learn Application Security",
    searchUrl = CoverSearchUrl.of("Alice and Bob Learn Application Security"),
)

@Preview(widthDp = 360, heightDp = 780)
@Composable
private fun CoverSearchScreenPreview() {
    CoverSearchPreviewOf(previewState)
}

@Preview(widthDp = 360, heightDp = 780)
@Composable
private fun CoverSearchScreenRejectedPreview() {
    CoverSearchPreviewOf(previewState.copy(isRejected = true))
}
