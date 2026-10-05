package com.paulchibamba.margin.domain.rollup

fun <K> Map<K, Int>.byCountDescending(): Map<K, Int> =
    entries.sortedByDescending { it.value }.associate { it.key to it.value }
