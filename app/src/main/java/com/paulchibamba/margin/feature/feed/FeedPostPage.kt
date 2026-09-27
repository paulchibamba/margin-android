package com.paulchibamba.margin.feature.feed

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.paulchibamba.margin.designsystem.MarginIcons
import com.paulchibamba.margin.designsystem.MarginTheme
import com.paulchibamba.margin.designsystem.Skins
import com.paulchibamba.margin.designsystem.component.ActionRail
import com.paulchibamba.margin.designsystem.component.ActionRailState
import com.paulchibamba.margin.designsystem.component.FeedLayout
import com.paulchibamba.margin.designsystem.component.FeedSnackbar
import com.paulchibamba.margin.designsystem.component.FeedTopBar
import com.paulchibamba.margin.designsystem.component.NudgeAction
import com.paulchibamba.margin.designsystem.component.NudgeCard
import com.paulchibamba.margin.designsystem.component.PostCaption
import com.paulchibamba.margin.domain.actions.Nudge
import com.paulchibamba.margin.domain.actions.PostAction
import com.paulchibamba.margin.domain.feed.CandidateSource
import kotlinx.coroutines.delay

private const val SNACKBAR_MILLIS = 4_000L

@Composable
fun FeedPostPage(
    page: FeedPage,
    streak: Int,
    nudge: FeedNudge?,
    onAction: (PostAction) -> Unit,
    onReadSource: () -> Unit,
    onReadAhead: () -> Unit,
    onMore: () -> Unit,
    onNudgeDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val snackbarNudge = nudge?.takeIf { it.kind == Nudge.TEST_COMING_SOON }
    SnackbarTimeout(snackbarNudge, onNudgeDismiss)
    MarginTheme(page.skin) {
        FeedLayout(
            topBar = { FeedTopBar(streak = streak, segments = null, onMoreClick = onMore) },
            body = { PostBody(page, onReadSource, onReadAhead) },
            caption = { PageCaption(page, nudge, onReadSource, onNudgeDismiss) },
            rail = { ActionRail(railStateOf(page), onAction) },
            modifier = modifier.fillMaxSize().background(page.skin.background),
            isSnackbarVisible = snackbarNudge != null,
            snackbar = { NudgeSnackbar(snackbarNudge) },
        )
    }
}

@Composable
private fun PageCaption(page: FeedPage, nudge: FeedNudge?, onReadSource: () -> Unit, onNudgeDismiss: () -> Unit) {
    if (nudge?.kind == Nudge.ANOTHER_ANGLE_COMING) {
        NudgeCard(
            title = nudge.title,
            detail = nudge.detail,
            primaryAction = NudgeAction("Book's words", MarginIcons.MenuBook, onReadSource),
            secondaryAction = NudgeAction("Keep going", onClick = onNudgeDismiss),
            modifier = Modifier.fillMaxWidth(),
        )
    } else {
        PostCaptionOf(page)
    }
}

@Composable
private fun PostCaptionOf(page: FeedPage) {
    val context = page.context
    PostCaption(
        formatLabel = page.item.post.format.label(),
        concept = context.conceptTitle,
        bookTitle = context.bookTitle,
        chapterLabel = chapterLabel(context.chapterNumber, context.chapterTitle),
        introduced = context.completion.introduced,
        total = context.completion.total,
        isReview = page.item.source == CandidateSource.REVIEW,
        isPreview = page.item.source == CandidateSource.PREVIEW,
    )
}

@Composable
private fun NudgeSnackbar(nudge: FeedNudge?) {
    if (nudge == null) return
    FeedSnackbar(nudge.title, nudge.detail, actionLabel = null, onAction = {}, modifier = Modifier.fillMaxWidth())
}

@Composable
private fun SnackbarTimeout(nudge: FeedNudge?, onTimeout: () -> Unit) {
    LaunchedEffect(nudge) {
        if (nudge != null) {
            delay(SNACKBAR_MILLIS)
            onTimeout()
        }
    }
}

private fun railStateOf(page: FeedPage): ActionRailState {
    val unavailable = if (page.context.sourceNote == null) setOf(PostAction.READ) else emptySet()
    return ActionRailState.of(page.viewState, unavailable)
}

@Preview(widthDp = 360, heightDp = 780)
@Composable
private fun FeedPostPageInkPreview() {
    FeedPostPage(FeedPreviewData.page(Skins.Ink), 7, null, {}, {}, {}, {}, {})
}

@Preview(widthDp = 360, heightDp = 780)
@Composable
private fun FeedPostPageLostPreview() {
    val nudge = FeedNudge.of(0, Nudge.ANOTHER_ANGLE_COMING, "CORS")
    FeedPostPage(FeedPreviewData.page(Skins.Cobalt, lost = true), 7, nudge, {}, {}, {}, {}, {})
}

@Preview(widthDp = 360, heightDp = 780)
@Composable
private fun FeedPostPageGotItPreview() {
    val nudge = FeedNudge.of(0, Nudge.TEST_COMING_SOON, "Server-side validation")
    FeedPostPage(FeedPreviewData.page(Skins.Paper), 7, nudge, {}, {}, {}, {}, {})
}
