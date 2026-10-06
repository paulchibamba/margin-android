package com.paulchibamba.margin.domain.progress

enum class Rejection {
    TITLE_TOO_LONG,
    BODY_TOO_LONG,
    BANNED_PATTERN,
    NUMBER_NOT_IN_FACTS,
    TITLE_NOT_IN_FACTS,
    QUOTE_NOT_VERBATIM,
    COPIED_FROM_EXCERPT,
    MARKUP,
}
