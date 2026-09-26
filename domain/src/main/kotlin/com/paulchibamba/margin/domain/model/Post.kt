package com.paulchibamba.margin.domain.model

data class Post(
    val id: PostId,
    val conceptId: ConceptId,
    val bookSlug: BookSlug,
    val content: PostContent,
) {
    val format: Format
        get() = content.format

    val role: PostRole
        get() = format.role
}
