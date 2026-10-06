package com.paulchibamba.margin.feature.drop

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.paulchibamba.margin.designsystem.MarginColors
import com.paulchibamba.margin.designsystem.MarginTypography
import com.paulchibamba.margin.designsystem.Skins

@Composable
fun DropContinuePage(modifier: Modifier = Modifier) {
    Box(modifier.fillMaxSize().background(Skins.Ink.background), contentAlignment = Alignment.Center) {
        Text("Back to your feed", style = MarginTypography.label, color = MarginColors.White.copy(alpha = 0.6f))
    }
}

@Preview(widthDp = 360, heightDp = 780)
@Composable
private fun DropContinuePagePreview() {
    DropContinuePage()
}
