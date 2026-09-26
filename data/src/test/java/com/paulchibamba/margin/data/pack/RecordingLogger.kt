package com.paulchibamba.margin.data.pack

class RecordingLogger : ImportLogger {
    val messages = mutableListOf<String>()

    override fun info(message: String) {
        messages += message
    }

    override fun warn(message: String) {
        messages += message
    }

    override fun error(message: String, cause: Throwable) {
        messages += "$message: ${cause.message}"
    }
}
