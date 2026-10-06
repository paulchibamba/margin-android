package com.paulchibamba.margin.domain.drop

data class DropStatus(val done: Int, val size: Int) {
    val isFinished: Boolean
        get() = done >= size
}
