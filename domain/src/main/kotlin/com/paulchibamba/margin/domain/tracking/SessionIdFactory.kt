package com.paulchibamba.margin.domain.tracking

fun interface SessionIdFactory {
    fun newSessionId(): SessionId
}
