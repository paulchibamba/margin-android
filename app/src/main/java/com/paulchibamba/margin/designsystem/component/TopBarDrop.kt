package com.paulchibamba.margin.designsystem.component

sealed interface TopBarDrop {
    data class Pill(val label: String, val onClick: () -> Unit) : TopBarDrop

    data class Progress(val label: String, val segments: SegmentProgress, val onClose: () -> Unit) : TopBarDrop
}
