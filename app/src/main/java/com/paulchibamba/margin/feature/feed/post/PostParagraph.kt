package com.paulchibamba.margin.feature.feed.post

import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.paulchibamba.margin.designsystem.LocalSkin
import com.paulchibamba.margin.designsystem.MarginTypography

@Composable
fun PostParagraph(text: String, modifier: Modifier = Modifier, style: TextStyle = MarginTypography.body) {
    Text(text, modifier.widthIn(max = 290.dp), style = style, color = LocalSkin.current.mutedContent)
}
