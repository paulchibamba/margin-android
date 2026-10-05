package com.paulchibamba.margin.feature.feed

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.paulchibamba.margin.domain.model.PostContent
import com.paulchibamba.margin.domain.tracking.InteractionKind
import com.paulchibamba.margin.feature.feed.post.AnalogyPost
import com.paulchibamba.margin.feature.feed.post.CarouselPost
import com.paulchibamba.margin.feature.feed.post.CarouselState
import com.paulchibamba.margin.feature.feed.post.ChecklistPost
import com.paulchibamba.margin.feature.feed.post.CodeExamplePost
import com.paulchibamba.margin.feature.feed.post.DialoguePost
import com.paulchibamba.margin.feature.feed.post.FillBlankPost
import com.paulchibamba.margin.feature.feed.post.FactPost
import com.paulchibamba.margin.feature.feed.post.McqPost
import com.paulchibamba.margin.feature.feed.post.MemePost
import com.paulchibamba.margin.feature.feed.post.MythPost
import com.paulchibamba.margin.feature.feed.post.PreviewTreatment
import com.paulchibamba.margin.feature.feed.post.RecallPost
import com.paulchibamba.margin.feature.feed.post.ScenarioPost
import com.paulchibamba.margin.feature.feed.post.SourcePost
import com.paulchibamba.margin.feature.feed.post.SpotBugPost
import com.paulchibamba.margin.feature.feed.post.TestPostState
import com.paulchibamba.margin.feature.feed.post.TipPost
import com.paulchibamba.margin.feature.feed.post.TrueFalsePost
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
        FormatBody(page.item.post.content, carousel, callbacks, page.testState, modifier)
    }
}

@Composable
private fun FormatBody(
    content: PostContent,
    carousel: CarouselState,
    callbacks: PostBodyCallbacks,
    test: TestPostState,
    modifier: Modifier,
) {
    when (content) {
        is PostContent.Tip -> TipPost(content, modifier)
        is PostContent.Fact -> FactPost(content, modifier)
        is PostContent.Analogy -> AnalogyPost(content, modifier)
        is PostContent.CodeExample -> CodeExamplePost(content, modifier)
        is PostContent.Meme -> MemePost(content, modifier)
        is PostContent.Source -> SourcePost(content, callbacks.onReadSource, modifier)
        is PostContent.Carousel -> CarouselPost(content, carousel, callbacks.onEngaged, modifier) {
            callbacks.onInteraction(InteractionKind.SLIDE)
        }
        is PostContent.Dialogue -> DialoguePost(content, modifier)
        is PostContent.Versus -> VersusPost(content, modifier)
        is PostContent.Myth -> MythPost(content, callbacks.engagedBy(InteractionKind.REVEAL), modifier)
        is PostContent.Checklist -> ChecklistPost(content, callbacks.engagedBy(InteractionKind.TICK), modifier)
        else -> TestBody(content, callbacks, test, modifier)
    }
}

@Composable
private fun TestBody(content: PostContent, callbacks: PostBodyCallbacks, test: TestPostState, modifier: Modifier) {
    val respond = callbacks.onRespond
    val reveal = callbacks.engagedBy(InteractionKind.REVEAL)
    when (content) {
        is PostContent.Mcq -> McqPost(content, test, respond, modifier)
        is PostContent.Scenario -> ScenarioPost(content, test, respond, modifier)
        is PostContent.SpotBug -> SpotBugPost(content, test, respond, modifier)
        is PostContent.TrueFalse -> TrueFalsePost(content, test, respond, modifier)
        is PostContent.Recall -> RecallPost(content, test, reveal, respond, modifier)
        is PostContent.FillBlank -> FillBlankPost(content, test, reveal, respond, modifier)
        else -> FallbackPostBody(content, modifier)
    }
}
