package com.paulchibamba.margin.feature.read.cover

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.paulchibamba.margin.designsystem.LocalSurfacePalette
import com.paulchibamba.margin.designsystem.MarginIcons
import com.paulchibamba.margin.designsystem.MarginTypography
import com.paulchibamba.margin.feature.read.ReadingProgressBar

private const val PERCENT = 100f

@Composable
fun CoverSearchTopBar(bookTitle: String, loadProgress: Int, onClose: () -> Unit, modifier: Modifier = Modifier) {
    val palette = LocalSurfacePalette.current
    Column(modifier.fillMaxWidth()) {
        Row(
            Modifier.fillMaxWidth().height(64.dp).padding(start = 4.dp, end = 18.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(onClick = onClose) {
                Icon(painterResource(MarginIcons.Close), "Close", Modifier.size(24.dp), palette.text)
            }
            TitleAndHint(bookTitle)
        }
        ReadingProgressBar(
            fraction = loadProgress / PERCENT,
            height = 3.dp,
            track = palette.divider,
            modifier = Modifier.alpha(if (loadProgress < PERCENT) 1f else 0f),
        )
    }
}

@Composable
private fun TitleAndHint(bookTitle: String) {
    val palette = LocalSurfacePalette.current
    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Text(
            "Cover for $bookTitle",
            style = MarginTypography.cardTitle,
            color = palette.text,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.semantics { heading() },
        )
        Text("Long-press an image to use it", style = MarginTypography.label, color = palette.faintText)
    }
}

@Preview(widthDp = 360, backgroundColor = 0xFFF3F0E9, showBackground = true)
@Composable
private fun CoverSearchTopBarPreview() {
    CoverSearchTopBar("Alice and Bob Learn Application Security", loadProgress = 40, onClose = {})
}
