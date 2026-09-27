package com.paulchibamba.margin.feature.feed.post

import androidx.compose.runtime.Composable
import com.paulchibamba.margin.designsystem.Skin
import com.paulchibamba.margin.domain.feed.CandidateSource
import com.paulchibamba.margin.domain.model.PostContent
import com.paulchibamba.margin.domain.usecase.ReadingAhead
import com.paulchibamba.margin.feature.feed.FeedPostPage
import com.paulchibamba.margin.feature.feed.FeedPreviewData
import com.paulchibamba.margin.feature.feed.PostBodyCallbacks

@Composable
internal fun PostPagePreview(
    skin: Skin,
    content: PostContent,
    source: CandidateSource = CandidateSource.NEW,
    readingAhead: ReadingAhead? = null,
    response: TestResponse? = null,
) {
    val page = FeedPreviewData.page(skin, content, source, readingAhead)
    FeedPostPage(
        page = page.copy(answer = response?.let { FeedPreviewData.answerTo(content, it) }),
        streak = 7,
        nudge = null,
        onAction = {},
        callbacks = PostBodyCallbacks(),
        onMore = {},
        onNudgeDismiss = {},
    )
}
