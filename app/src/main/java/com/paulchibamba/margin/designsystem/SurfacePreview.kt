package com.paulchibamba.margin.designsystem

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier

@Composable
fun SurfacePreview(palette: SurfacePalette, content: @Composable () -> Unit) {
    CompositionLocalProvider(LocalSurfacePalette provides palette) {
        MarginTheme(palette.skin) {
            Box(Modifier.background(palette.background)) { content() }
        }
    }
}
