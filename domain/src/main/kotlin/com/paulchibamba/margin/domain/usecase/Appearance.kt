package com.paulchibamba.margin.domain.usecase

import com.paulchibamba.margin.domain.model.DarkMode
import com.paulchibamba.margin.domain.model.FeedTone

data class Appearance(val darkMode: DarkMode, val isDarkPostsOn: Boolean) {

    fun feedTone(isSystemDark: Boolean): FeedTone = when {
        isDarkPostsOn -> FeedTone.NIGHT
        darkMode.isDark(isSystemDark) -> FeedTone.DARK
        else -> FeedTone.ANY
    }
}
