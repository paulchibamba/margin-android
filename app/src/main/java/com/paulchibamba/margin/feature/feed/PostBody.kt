package com.paulchibamba.margin.feature.feed

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.paulchibamba.margin.domain.feed.CandidateSource
import com.paulchibamba.margin.domain.model.PostContent
import com.paulchibamba.margin.feature.feed.post.AnalogyPost
import com.paulchibamba.margin.feature.feed.post.CodeExamplePost
import com.paulchibamba.margin.feature.feed.post.FactPost
import com.paulchibamba.margin.feature.feed.post.MemePost
import com.paulchibamba.margin.feature.feed.post.PreviewTreatment
import com.paulchibamba.margin.feature.feed.post.SourcePost
import com.paulchibamba.margin.feature.feed.post.TipPost

@Composable
fun PostBody(page: FeedPage, onReadSource: () -> Unit, onReadAhead: () -> Unit, modifier: Modifier = Modifier) {
    val content = page.item.post.content
    val readingAhead = page.context.readingAhead
    if (page.item.source == CandidateSource.PREVIEW && readingAhead != null) {
        PreviewTreatment(content, readingAhead, onReadAhead, modifier)
    } else {
        FormatBody(content, onReadSource, modifier)
    }
}

@Composable
private fun FormatBody(content: PostContent, onReadSource: () -> Unit, modifier: Modifier) {
    when (content) {
        is PostContent.Tip -> TipPost(content, modifier)
        is PostContent.Fact -> FactPost(content, modifier)
        is PostContent.Analogy -> AnalogyPost(content, modifier)
        is PostContent.CodeExample -> CodeExamplePost(content, modifier)
        is PostContent.Meme -> MemePost(content, modifier)
        is PostContent.Source -> SourcePost(content, onReadSource, modifier)
        else -> FallbackPostBody(content, modifier)
    }
}
