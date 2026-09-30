package com.paulchibamba.margin.feature.settings

import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.paulchibamba.margin.designsystem.LocalSurfacePalette
import com.paulchibamba.margin.designsystem.SurfacePalette

@Composable
fun settingsSegmentColors(palette: SurfacePalette = LocalSurfacePalette.current) = SegmentedButtonDefaults.colors(
    activeContainerColor = palette.accent,
    activeContentColor = palette.onAccent,
    activeBorderColor = palette.text.copy(alpha = 0.2f),
    inactiveContainerColor = Color.Transparent,
    inactiveContentColor = palette.text,
    inactiveBorderColor = palette.text.copy(alpha = 0.2f),
)
