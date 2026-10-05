package com.paulchibamba.margin.domain.progress

import com.paulchibamba.margin.domain.model.Format

data class Angle(val format: String, val title: String) {

    val label: String
        get() = "$format: $title"

    companion object {
        fun of(format: Format, title: String) = Angle(format.name.lowercase(), title)
    }
}
