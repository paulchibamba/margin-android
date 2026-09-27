package com.paulchibamba.margin.feature.feed

enum class FeedNudgeKind(val isSnackbar: Boolean) {
    ANOTHER_ANGLE_COMING(isSnackbar = false),
    TEST_COMING_SOON(isSnackbar = true),
    SEE_AGAIN(isSnackbar = true),
}
