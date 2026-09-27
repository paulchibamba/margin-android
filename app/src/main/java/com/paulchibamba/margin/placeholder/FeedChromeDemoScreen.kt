package com.paulchibamba.margin.placeholder

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.paulchibamba.margin.designsystem.LocalSkin
import com.paulchibamba.margin.designsystem.MarginIcons
import com.paulchibamba.margin.designsystem.MarginTheme
import com.paulchibamba.margin.designsystem.MarginTypography
import com.paulchibamba.margin.designsystem.Skin
import com.paulchibamba.margin.designsystem.SkinRotation
import com.paulchibamba.margin.designsystem.StatusBarFollowsSkin
import com.paulchibamba.margin.designsystem.component.ActionRail
import com.paulchibamba.margin.designsystem.component.ActionRailState
import com.paulchibamba.margin.designsystem.component.FeedLayout
import com.paulchibamba.margin.designsystem.component.FeedSnackbar
import com.paulchibamba.margin.designsystem.component.FeedTopBar
import com.paulchibamba.margin.designsystem.component.NudgeAction
import com.paulchibamba.margin.designsystem.component.NudgeCard
import com.paulchibamba.margin.designsystem.component.PostCaption
import com.paulchibamba.margin.designsystem.component.SegmentProgress
import com.paulchibamba.margin.domain.actions.PostAction
import com.paulchibamba.margin.domain.actions.PostViewState
import com.paulchibamba.margin.domain.feed.Confidence
import kotlinx.coroutines.delay
import kotlin.random.Random

private const val PAGE_COUNT = 12
private const val SNACKBAR_MILLIS = 4_000L

@Composable
fun FeedChromeDemoScreen(onMoreClick: () -> Unit, onReadClick: () -> Unit, modifier: Modifier = Modifier) {
    val skins = remember { demoSkins() }
    val pagerState = rememberPagerState { PAGE_COUNT }
    MarginTheme(skins[pagerState.currentPage]) {
        StatusBarFollowsSkin()
        VerticalPager(pagerState, modifier.fillMaxSize(), beyondViewportPageCount = 1) { page ->
            MarginTheme(skins[page]) { DemoPost(page, onMoreClick, onReadClick) }
        }
    }
}

private fun demoSkins(): List<Skin> {
    val rotation = SkinRotation(Random(seed = 16))
    return generateSequence(rotation.next(null)) { rotation.next(it) }.take(PAGE_COUNT).toList()
}

@Composable
private fun DemoPost(page: Int, onMoreClick: () -> Unit, onReadClick: () -> Unit) {
    var view by remember { mutableStateOf(PostViewState()) }
    var isSnackbarVisible by rememberSaveable { mutableStateOf(false) }
    SnackbarTimeout(isSnackbarVisible) { isSnackbarVisible = false }
    val onAction: (PostAction) -> Unit = { action ->
        if (action == PostAction.READ) onReadClick()
        if (action == PostAction.GOT && view.canChoose(action)) isSnackbarVisible = true
        view = view.afterChoosing(action)
    }
    FeedLayout(
        topBar = { FeedTopBar(streak = 7, segments = segmentsFor(page), onMoreClick = onMoreClick) },
        body = { DemoBody(page) },
        caption = { DemoCaption(page, isLost = view.confidence == Confidence.LOST) { onReadClick() } },
        rail = { ActionRail(ActionRailState.of(view), onAction) },
        modifier = Modifier.fillMaxSize().background(LocalSkin.current.background),
        isSnackbarVisible = isSnackbarVisible,
        snackbar = {
            FeedSnackbar("You'll see this again in ~10 min", "Alice & Bob · Ch 4 · 15/48", "Read page", onReadClick)
        },
    )
}

@Composable
private fun SnackbarTimeout(isVisible: Boolean, onTimeout: () -> Unit) {
    LaunchedEffect(isVisible) {
        if (isVisible) {
            delay(SNACKBAR_MILLIS)
            onTimeout()
        }
    }
}

private fun segmentsFor(page: Int): SegmentProgress? = if (page % 3 == 1) SegmentProgress(4, 1) else null

@Composable
private fun DemoBody(page: Int) {
    val skin = LocalSkin.current
    Column(Modifier.padding(start = 22.dp, top = 48.dp), verticalArrangement = Arrangement.spacedBy(18.dp)) {
        Text("Never trust the client.", style = MarginTypography.postHeadline, color = skin.content)
        Text(
            "Post ${page + 1} · ${skin.name}. Client-side checks are for UX, not security.",
            style = MarginTypography.body,
            color = skin.mutedContent,
            modifier = Modifier.widthIn(max = 290.dp),
        )
    }
}

@Composable
private fun DemoCaption(page: Int, isLost: Boolean, onBooksWords: () -> Unit) {
    if (isLost) {
        NudgeCard(
            title = "Got it. A different angle is next.",
            detail = "No quiz on CORS until it clicks.",
            primaryAction = NudgeAction("Book's words", MarginIcons.MenuBook, onBooksWords),
            secondaryAction = NudgeAction("Keep going") {},
            modifier = Modifier.fillMaxWidth(),
        )
    } else {
        PostCaption("Tip", "Server-side validation", "Alice & Bob Learn AppSec", "Ch 3 · Input", 12, 48,
            isReview = page % 4 == 2, isPreview = page % 5 == 3)
    }
}
