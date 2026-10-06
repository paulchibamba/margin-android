package com.paulchibamba.margin.domain.usecase

import com.paulchibamba.margin.domain.model.Post
import com.paulchibamba.margin.domain.model.PostId
import com.paulchibamba.margin.domain.repository.ContentRepository
import com.paulchibamba.margin.domain.repository.GeneratedPostRepository
import javax.inject.Inject

class DropPosts @Inject constructor(
    private val content: ContentRepository,
    private val generatedPosts: GeneratedPostRepository,
) {
    suspend fun byId(): Map<PostId, Post> {
        val books = content.concepts().associate { concept -> concept.id to concept.bookSlug }
        val generated = generatedPosts.all().mapNotNull { post ->
            post.conceptIds.firstOrNull()?.let(books::get)?.let(post::toPost)
        }
        return (content.posts() + generated).associateBy(Post::id)
    }
}
