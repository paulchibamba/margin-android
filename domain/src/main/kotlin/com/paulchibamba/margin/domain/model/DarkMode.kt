package com.paulchibamba.margin.domain.model

enum class DarkMode {
    OFF,
    FOLLOW_SYSTEM,
    ALWAYS,
    ;

    fun isDark(isSystemDark: Boolean): Boolean = when (this) {
        OFF -> false
        FOLLOW_SYSTEM -> isSystemDark
        ALWAYS -> true
    }

    companion object {
        val DEFAULT = OFF

        fun fromName(name: String?): DarkMode = entries.firstOrNull { it.name == name } ?: DEFAULT
    }
}
