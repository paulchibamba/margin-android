package com.paulchibamba.margin.domain.usecase

import com.paulchibamba.margin.domain.model.PostId
import com.paulchibamba.margin.domain.progress.GeneratedPost
import com.paulchibamba.margin.domain.repository.GeneratedPostRepository
import java.time.Instant

class FakeGeneratedPostRepository : GeneratedPostRepository {
    val posts = mutableListOf<GeneratedPost>()

    override suspend fun insert(posts: List<GeneratedPost>): Int {
        val new = posts.filter { post -> this.posts.none { it.factsHash == post.factsHash } }
        this.posts += new
        return new.size
    }

    override suspend fun fresh(now: Instant): List<GeneratedPost> = posts.filter { post ->
        !post.isShown && !post.isLessPressed && post.expiresAt?.isAfter(now) != false
    }

    override suspend fun all(): List<GeneratedPost> = posts.toList()

    override suspend fun markShown(post: PostId, at: Instant) = update(post) { it.copy(shownAt = it.shownAt ?: at) }

    override suspend fun markLess(post: PostId) = update(post) { it.copy(isLessPressed = true) }

    private fun update(post: PostId, change: (GeneratedPost) -> GeneratedPost) {
        val index = posts.indexOfFirst { it.id == post }
        if (index >= 0) posts[index] = change(posts[index])
    }
}
