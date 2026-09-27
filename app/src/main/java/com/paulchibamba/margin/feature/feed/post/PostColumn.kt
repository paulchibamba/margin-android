package com.paulchibamba.margin.feature.feed.post

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

const val POST_BODY_TAG = "post-body"

@Composable
fun PostColumn(
    modifier: Modifier = Modifier,
    top: Dp = 40.dp,
    spacing: Dp = 16.dp,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(
        modifier
            .fillMaxSize()
            .testTag(POST_BODY_TAG)
            .verticalScroll(rememberScrollState())
            .padding(start = 22.dp, top = top, bottom = 16.dp),
        verticalArrangement = Arrangement.spacedBy(spacing),
        content = content,
    )
}
