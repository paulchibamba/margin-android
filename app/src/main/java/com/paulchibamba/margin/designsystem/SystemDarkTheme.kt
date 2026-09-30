package com.paulchibamba.margin.designsystem

import android.content.Context
import android.content.res.Configuration
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class SystemDarkTheme @Inject constructor(@ApplicationContext private val context: Context) {

    fun isOn(): Boolean =
        context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES
}
