package com.paulchibamba.margin.domain.llm

enum class LlmFailureReason {
    KEY_REJECTED,
    REQUEST_REJECTED,
    RATE_LIMITED,
    SERVER_ERROR,
    TIMED_OUT,
    OFFLINE,
    REFUSED,
    INCOMPLETE,
    UNREADABLE,
}
