package com.paulchibamba.margin.feature.stats

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.paulchibamba.margin.designsystem.MarginColors
import com.paulchibamba.margin.designsystem.MarginTypography

@Composable
fun EmptyStatsLine(text: String, modifier: Modifier = Modifier) {
    Text(text, style = MarginTypography.label, color = MarginColors.White.copy(alpha = 0.6f), modifier = modifier)
}
