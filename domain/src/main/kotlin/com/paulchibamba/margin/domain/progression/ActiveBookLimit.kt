package com.paulchibamba.margin.domain.progression

object ActiveBookLimit {
    val ALLOWED = 1..3

    fun allows(activeCount: Int): Boolean = activeCount in ALLOWED
}
