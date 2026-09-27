package com.paulchibamba.margin.feature.feed

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.paulchibamba.margin.domain.model.PostContent
import com.paulchibamba.margin.feature.feed.post.AnalogyPost
import com.paulchibamba.margin.feature.feed.post.CarouselPost
import com.paulchibamba.margin.feature.feed.post.CarouselState
import com.paulchibamba.margin.feature.feed.post.ChecklistPost
import com.paulchibamba.margin.feature.feed.post.CodeExamplePost
import com.paulchibamba.margin.feature.feed.post.DialoguePost
import com.paulchibamba.margin.feature.feed.post.FactPost
import com.paulchibamba.margin.feature.feed.post.MemePost
import com.paulchibamba.margin.feature.feed.post.MythPost
import com.paulchibamba.margin.feature.feed.post.PreviewTreatment
import com.paulchibamba.margin.feature.feed.post.SourcePost
import com.paulchibamba.margin.feature.feed.post.TipPost
import com.paulchibamba.margin.feature.feed.post.VersusPost

@Composable
fun PostBody(
    page: FeedPage,
    carousel: CarouselState,
    callbacks: PostBodyCallbacks,
    modifier: Modifier = Modifier,
) {
    val readingAhead = page.context.readingAhead
    if (page.isLockedPreview && readingAhead != null) {
        PreviewTreatment(page.item.post.content, readingAhead, callbacks.onReadAhead, modifier)
    } else {
        FormatBody(page.item.post.content, carousel, callbacks, modifier)
    }
}

@Composable
private fun FormatBody(
    content: PostContent,
    carousel: CarouselState,
    callbacks: PostBodyCallbacks,
    modifier: Modifier,
) {
    when (content) {
        is PostContent.Tip -> TipPost(content, modifier)
        is PostContent.Fact -> FactPost(content, modifier)
        is PostContent.Analogy -> AnalogyPost(content, modifier)
        is PostContent.CodeExample -> CodeExamplePost(content, modifier)
        is PostContent.Meme -> MemePost(content, modifier)
        is PostContent.Source -> SourcePost(content, callbacks.onReadSource, modifier)
        is PostContent.Carousel -> CarouselPost(content, carousel, callbacks.onEngaged, modifier)
        is PostContent.Dialogue -> DialoguePost(content, modifier)
        is PostContent.Versus -> VersusPost(content, modifier)
        is PostContent.Myth -> MythPost(content, callbacks.onEngaged, modifier)
        is PostContent.Checklist -> ChecklistPost(content, callbacks.onEngaged, modifier)
        else -> FallbackPostBody(content, modifier)
    }
}
