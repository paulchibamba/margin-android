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
import com.paulchibamba.margin.designsystem.component.SegmentProgress
import com.paulchibamba.margin.domain.actions.Nudge
import com.paulchibamba.margin.domain.actions.PostAction
import com.paulchibamba.margin.domain.feed.CandidateSource
import com.paulchibamba.margin.domain.model.PostContent
import com.paulchibamba.margin.feature.feed.post.CarouselState
import com.paulchibamba.margin.feature.feed.post.TestResponse
import com.paulchibamba.margin.feature.feed.post.rememberCarouselState
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.minutes

private const val SNACKBAR_MILLIS = 4_000L

@Composable
fun FeedPostPage(
    page: FeedPage,
    streak: Int,
    nudge: FeedNudge?,
    onAction: (PostAction) -> Unit,
    callbacks: PostBodyCallbacks,
    onMore: () -> Unit,
    onNudgeDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val snackbarNudge = nudge?.takeIf { it.kind.isSnackbar }
    SnackbarTimeout(snackbarNudge, onNudgeDismiss)
    val carousel = rememberCarouselState(page.item.post.id, slideCountOf(page))
    MarginTheme(page.skin) {
        FeedLayout(
            topBar = { FeedTopBar(streak = streak, segments = segmentsOf(page, carousel), onMoreClick = onMore) },
            body = { PostBody(page, carousel, callbacks) },
            caption = { PageCaption(page, nudge, callbacks.onReadSource, onNudgeDismiss) },
            rail = { ActionRail(railStateOf(page), onAction) },
            modifier = modifier.fillMaxSize().background(page.skin.background),
            isSnackbarVisible = snackbarNudge != null,
            snackbar = { NudgeSnackbar(snackbarNudge, readPageActionOf(page, callbacks)) },
        )
    }
}

@Composable
private fun PageCaption(page: FeedPage, nudge: FeedNudge?, onReadSource: () -> Unit, onNudgeDismiss: () -> Unit) {
    if (nudge?.kind == FeedNudgeKind.ANOTHER_ANGLE_COMING) {
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
private fun NudgeSnackbar(nudge: FeedNudge?, readPage: (() -> Unit)?) {
    if (nudge == null) return
    val action = readPage?.takeIf { nudge.kind == FeedNudgeKind.SEE_AGAIN }
    FeedSnackbar(
        message = nudge.title,
        detail = nudge.detail,
        actionLabel = action?.let { "Read page" },
        onAction = { action?.invoke() },
        modifier = Modifier.fillMaxWidth(),
    )
}

private fun readPageActionOf(page: FeedPage, callbacks: PostBodyCallbacks): (() -> Unit)? =
    callbacks.onReadSource.takeIf { page.context.sourceNote != null }

@Composable
private fun SnackbarTimeout(nudge: FeedNudge?, onTimeout: () -> Unit) {
    LaunchedEffect(nudge) {
        if (nudge != null) {
            delay(SNACKBAR_MILLIS)
            onTimeout()
        }
    }
}

private fun slideCountOf(page: FeedPage): Int =
    (page.item.post.content as? PostContent.Carousel)?.slides?.size ?: 1

private fun segmentsOf(page: FeedPage, carousel: CarouselState): SegmentProgress? =
    carousel.segments.takeIf { page.item.post.content is PostContent.Carousel && !page.isLockedPreview }

private fun railStateOf(page: FeedPage): ActionRailState {
    val unavailable = if (page.context.sourceNote == null) setOf(PostAction.READ) else emptySet()
    return ActionRailState.of(page.viewState, unavailable)
}

@Preview(widthDp = 360, heightDp = 780)
@Composable
private fun FeedPostPageInkPreview() {
    FeedPostPage(FeedPreviewData.page(Skins.Ink), 7, null, {}, PostBodyCallbacks(), {}, {})
}

@Preview(widthDp = 360, heightDp = 780)
@Composable
private fun FeedPostPageLostPreview() {
    val nudge = FeedNudge.of(0, Nudge.ANOTHER_ANGLE_COMING, "CORS")
    FeedPostPage(FeedPreviewData.page(Skins.Cobalt, lost = true), 7, nudge, {}, PostBodyCallbacks(), {}, {})
}

@Preview(widthDp = 360, heightDp = 780)
@Composable
private fun FeedPostPageGotItPreview() {
    val nudge = FeedNudge.of(0, Nudge.TEST_COMING_SOON, "Server-side validation")
    FeedPostPage(FeedPreviewData.page(Skins.Paper), 7, nudge, {}, PostBodyCallbacks(), {}, {})
}

@Preview(widthDp = 360, heightDp = 780)
@Composable
private fun FeedPostPageAnsweredWrongPreview() {
    val content = PostContent.Mcq(
        title = "Stored XSS",
        question = "Which control stops stored XSS in an HTML body?",
        options = listOf("Input length limit", "Context-aware output encoding", "HTTPS everywhere", "Block <script>"),
        answerIndex = 1,
        explanation = "Filters miss <img onerror> and hundreds of other vectors. Encoding stops them all at output.",
    )
    val page = FeedPreviewData.page(Skins.Ink, content, CandidateSource.REVIEW, readingAhead = null)
    val answered = page.copy(answer = FeedPreviewData.answerTo(content, TestResponse.Choice(3)))
    val nudge = FeedNudge.seeAgain(0, 10.minutes, page.context)
    FeedPostPage(answered, 7, nudge, {}, PostBodyCallbacks(), {}, {})
}
