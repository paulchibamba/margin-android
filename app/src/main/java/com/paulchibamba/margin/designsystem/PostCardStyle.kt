package com.paulchibamba.margin.designsystem

import androidx.compose.runtime.staticCompositionLocalOf

val LocalPostCardStyle = staticCompositionLocalOf { PostCardStyle.CONTRASTING }

enum class PostCardStyle {
    CONTRASTING,
    DARK,
    ;

    companion object {
        fun of(isDarkPostsOn: Boolean): PostCardStyle = if (isDarkPostsOn) DARK else CONTRASTING
    }
}
