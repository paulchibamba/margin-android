package com.paulchibamba.margin.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.paulchibamba.margin.designsystem.LocalSkin
import com.paulchibamba.margin.designsystem.MarginIcons
import com.paulchibamba.margin.designsystem.MarginTheme
import com.paulchibamba.margin.designsystem.MarginTypography
import com.paulchibamba.margin.designsystem.Skin
import com.paulchibamba.margin.designsystem.Skins
import com.paulchibamba.margin.domain.actions.PostAction
import com.paulchibamba.margin.domain.feed.Confidence

@Composable
private fun SampleBody(headline: String, text: String) {
    val skin = LocalSkin.current
    Column(Modifier.padding(start = 22.dp, top = 48.dp), verticalArrangement = Arrangement.spacedBy(18.dp)) {
        Text(headline, style = MarginTypography.postHeadline, color = skin.content)
        Text(text, style = MarginTypography.body, color = skin.mutedContent, modifier = Modifier.widthIn(max = 290.dp))
    }
}

@Composable
private fun SampleCaption(isReview: Boolean = false) {
    PostCaption(
        formatLabel = "Tip",
        concept = "Server-side validation",
        bookTitle = "Alice & Bob Learn AppSec",
        chapterLabel = "Ch 3 · Input",
        introduced = 12,
        total = 48,
        isReview = isReview,
        isPreview = false,
    )
}

@Composable
private fun SampleFeed(
    skin: Skin,
    railState: ActionRailState = ActionRailState(),
    isSnackbarVisible: Boolean = false,
    caption: @Composable () -> Unit = { SampleCaption() },
) {
    MarginTheme(skin) {
        FeedLayout(
            topBar = { FeedTopBar(streak = 7, segments = null, onMoreClick = {}) },
            body = {
                SampleBody(
                    "Never trust the client.",
                    "Client-side checks are for UX, not security. Anyone with an intercepting proxy skips them.",
                )
            },
            caption = caption,
            rail = { ActionRail(railState, onAction = {}) },
            modifier = Modifier.fillMaxSize().background(skin.background),
            isSnackbarVisible = isSnackbarVisible,
            snackbar = {
                FeedSnackbar("You'll see this again in ~10 min", "Alice & Bob · Ch 4 · 15/48", "Read page", {})
            },
        )
    }
}

@Preview(name = "2a Tip on Ink", widthDp = 360, heightDp = 703)
@Composable
private fun FeedLayoutInkPreview() = SampleFeed(Skins.Ink)

@Preview(name = "2c on Paper, Save active", widthDp = 360, heightDp = 703)
@Composable
private fun FeedLayoutPaperPreview() = SampleFeed(Skins.Paper, ActionRailState(isSaved = true))

@Preview(name = "2e snackbar, rail moved up", widthDp = 360, heightDp = 703)
@Composable
private fun FeedLayoutSnackbarPreview() = SampleFeed(Skins.Ink, isSnackbarVisible = true)

@Preview(name = "2i reteach nudge on Cobalt", widthDp = 360, heightDp = 703)
@Composable
private fun FeedLayoutNudgePreview() = SampleFeed(
    skin = Skins.Cobalt,
    railState = ActionRailState(confidence = Confidence.LOST, disabledActions = setOf(PostAction.GOT)),
    caption = {
        NudgeCard(
            title = "Got it. A different angle is next.",
            detail = "No quiz on CORS until it clicks.",
            primaryAction = NudgeAction("Book's words", MarginIcons.MenuBook) {},
            secondaryAction = NudgeAction("Keep going") {},
            modifier = Modifier.fillMaxWidth(),
        )
    },
)

@Preview(name = "430 dp wide on Forest", widthDp = 430, heightDp = 850)
@Composable
private fun FeedLayoutWidePreview() = SampleFeed(Skins.Forest, caption = { SampleCaption(isReview = true) })
