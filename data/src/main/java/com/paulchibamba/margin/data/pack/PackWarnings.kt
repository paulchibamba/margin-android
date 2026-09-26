package com.paulchibamba.margin.data.pack

data class PackWarnings(
    val conceptsWithoutTeachPost: List<String> = emptyList(),
    val conceptsWithoutTestPost: List<String> = emptyList(),
) {
    val isEmpty: Boolean
        get() = conceptsWithoutTeachPost.isEmpty() && conceptsWithoutTestPost.isEmpty()
}
