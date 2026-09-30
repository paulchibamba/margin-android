package com.paulchibamba.margin.feature.debug

import android.content.Context
import android.content.Intent
import kotlin.system.exitProcess

object AppRestart {

    fun restart(context: Context) {
        val launch = context.packageManager.getLaunchIntentForPackage(context.packageName) ?: return
        context.startActivity(launch.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK))
        exitProcess(0)
    }
}
