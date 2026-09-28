package com.paulchibamba.margin.feature.settings

import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.paulchibamba.margin.designsystem.MarginColors

@Composable
fun SettingsDivider(modifier: Modifier = Modifier) {
    HorizontalDivider(modifier, thickness = 1.dp, color = MarginColors.InkText.copy(alpha = 0.08f))
}
