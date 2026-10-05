package com.paulchibamba.margin.domain.rollup

object Share {

    fun of(part: Int, whole: Int): Double? = if (whole == 0) null else part.toDouble() / whole
}
