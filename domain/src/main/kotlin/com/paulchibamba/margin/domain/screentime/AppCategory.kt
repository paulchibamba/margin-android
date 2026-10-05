package com.paulchibamba.margin.domain.screentime

enum class AppCategory(val isDoomByDefault: Boolean) {
    SOCIAL(isDoomByDefault = true),
    VIDEO(isDoomByDefault = true),
    GAME(isDoomByDefault = true),
    AUDIO(isDoomByDefault = false),
    IMAGE(isDoomByDefault = false),
    NEWS(isDoomByDefault = false),
    MAPS(isDoomByDefault = false),
    PRODUCTIVITY(isDoomByDefault = false),
    ACCESSIBILITY(isDoomByDefault = false),
    OTHER(isDoomByDefault = false),
    MARGIN(isDoomByDefault = false),
}
