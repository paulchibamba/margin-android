package com.paulchibamba.margin.feature.settings.screentime

import com.paulchibamba.margin.domain.screentime.AppCategory
import com.paulchibamba.margin.domain.screentime.AppScreenTime
import com.paulchibamba.margin.domain.screentime.PackageName
import kotlin.time.Duration.Companion.minutes

object DoomAppsPreviewData {
    val apps = listOf(
        AppScreenTime(PackageName("com.instagram.android"), "Instagram", AppCategory.SOCIAL, true, 400.minutes),
        AppScreenTime(PackageName("com.google.android.youtube"), "YouTube", AppCategory.VIDEO, true, 185.minutes),
        AppScreenTime(PackageName("com.whatsapp"), "WhatsApp", AppCategory.SOCIAL, false, 92.minutes),
        AppScreenTime(PackageName("com.android.chrome"), "Chrome", AppCategory.OTHER, false, 41.minutes),
    )
}
