package com.paulchibamba.margin.data.pack

interface ImportLogger {
    fun info(message: String)
    fun warn(message: String)
    fun error(message: String, cause: Throwable)
}
