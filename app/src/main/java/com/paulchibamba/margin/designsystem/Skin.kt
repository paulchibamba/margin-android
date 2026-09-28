package com.paulchibamba.margin.designsystem

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

@Immutable
data class Skin(
    val name: String,
    val background: Color,
    val content: Color,
    val mutedContent: Color,
    val surface: Color,
    val correct: Color,
    val wrong: Color,
    val save: Color,
    val isLight: Boolean,
)
