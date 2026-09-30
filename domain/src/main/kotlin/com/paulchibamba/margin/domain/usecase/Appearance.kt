package com.paulchibamba.margin.domain.usecase

import com.paulchibamba.margin.domain.model.DarkMode

data class Appearance(val darkMode: DarkMode, val isDarkPostsOn: Boolean) {

    fun isFeedDarkOnly(isSystemDark: Boolean): Boolean = isDarkPostsOn || darkMode.isDark(isSystemDark)
}
