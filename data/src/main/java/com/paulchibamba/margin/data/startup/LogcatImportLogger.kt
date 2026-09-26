package com.paulchibamba.margin.data.startup

import android.util.Log
import com.paulchibamba.margin.data.pack.ImportLogger

class LogcatImportLogger : ImportLogger {
    override fun info(message: String) {
        Log.i(TAG, message)
    }

    override fun warn(message: String) {
        Log.w(TAG, message)
    }

    override fun error(message: String, cause: Throwable) {
        Log.e(TAG, message, cause)
    }

    private companion object {
        const val TAG = "MarginPack"
    }
}
