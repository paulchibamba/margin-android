package com.paulchibamba.margin.designsystem

import androidx.compose.ui.tooling.preview.PreviewParameterProvider

class SurfacePaletteProvider : PreviewParameterProvider<SurfacePalette> {
    override val values: Sequence<SurfacePalette> = SurfacePalette.all.asSequence()
}
