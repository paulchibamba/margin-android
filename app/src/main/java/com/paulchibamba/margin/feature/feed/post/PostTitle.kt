package com.paulchibamba.margin.feature.feed.post

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import com.paulchibamba.margin.designsystem.LocalSkin
import com.paulchibamba.margin.designsystem.MarginTypography

private const val HEADLINE_MAX_CHARACTERS = 26

@Composable
fun PostTitle(title: String, modifier: Modifier = Modifier, style: TextStyle = MarginTypography.postTitle) {
    Text(title, modifier, style = style, color = LocalSkin.current.content)
}

fun headlineStyleFor(title: String): TextStyle =
    if (title.length <= HEADLINE_MAX_CHARACTERS) MarginTypography.postHeadline else MarginTypography.postTitle
