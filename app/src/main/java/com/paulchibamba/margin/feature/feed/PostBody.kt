package com.paulchibamba.margin.feature.feed

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.paulchibamba.margin.domain.model.PostContent

@Composable
fun PostBody(content: PostContent, modifier: Modifier = Modifier) {
    FallbackPostBody(content, modifier)
}
