package com.paulchibamba.margin.domain.tracking

interface PresenceListener {
    fun onInput()
    fun onForegroundChanged(foreground: Boolean)
    fun onInteractiveChanged(interactive: Boolean)
}
