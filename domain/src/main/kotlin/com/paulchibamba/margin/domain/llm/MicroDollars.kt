package com.paulchibamba.margin.domain.llm

@JvmInline
value class MicroDollars(val value: Long) : Comparable<MicroDollars> {

    operator fun plus(other: MicroDollars): MicroDollars = MicroDollars(value + other.value)

    override fun compareTo(other: MicroDollars): Int = value.compareTo(other.value)

    companion object {
        val ZERO = MicroDollars(0)
        const val PER_DOLLAR = 1_000_000L

        fun ofDollars(dollars: Double): MicroDollars = MicroDollars(Math.round(dollars * PER_DOLLAR))
    }
}
