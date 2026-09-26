package com.paulchibamba.margin.data.repository.mapper

import com.paulchibamba.margin.domain.model.Format

object FormatNames {
    fun nameOf(format: Format): String = format.name.lowercase()

    fun formatOf(name: String): Format = Format.valueOf(name.uppercase())
}
