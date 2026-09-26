package com.paulchibamba.margin.designsystem.component

import androidx.annotation.DrawableRes
import com.paulchibamba.margin.designsystem.MarginIcons

enum class NavigationTab(val label: String, @param:DrawableRes val activeIcon: Int, @param:DrawableRes val icon: Int) {
    Feed("Feed", MarginIcons.PlayCircle, MarginIcons.PlayCircleOutlined),
    Read("Read", MarginIcons.AutoStories, MarginIcons.AutoStoriesOutlined),
}
