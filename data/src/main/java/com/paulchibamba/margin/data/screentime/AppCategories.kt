package com.paulchibamba.margin.data.screentime

import android.content.pm.ApplicationInfo
import com.paulchibamba.margin.domain.screentime.AppCategory

object AppCategories {

    fun of(category: Int): AppCategory = when (category) {
        ApplicationInfo.CATEGORY_SOCIAL -> AppCategory.SOCIAL
        ApplicationInfo.CATEGORY_VIDEO -> AppCategory.VIDEO
        ApplicationInfo.CATEGORY_GAME -> AppCategory.GAME
        ApplicationInfo.CATEGORY_AUDIO -> AppCategory.AUDIO
        ApplicationInfo.CATEGORY_IMAGE -> AppCategory.IMAGE
        ApplicationInfo.CATEGORY_NEWS -> AppCategory.NEWS
        ApplicationInfo.CATEGORY_MAPS -> AppCategory.MAPS
        ApplicationInfo.CATEGORY_PRODUCTIVITY -> AppCategory.PRODUCTIVITY
        ApplicationInfo.CATEGORY_ACCESSIBILITY -> AppCategory.ACCESSIBILITY
        else -> AppCategory.OTHER
    }
}
