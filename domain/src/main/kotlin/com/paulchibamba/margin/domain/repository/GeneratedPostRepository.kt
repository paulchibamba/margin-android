package com.paulchibamba.margin.domain.repository

import com.paulchibamba.margin.domain.model.PostId
import com.paulchibamba.margin.domain.progress.GeneratedPost
import java.time.Instant

interface GeneratedPostRepository {
    suspend fun insert(posts: List<GeneratedPost>): Int
    suspend fun fresh(now: Instant): List<GeneratedPost>
    suspend fun all(): List<GeneratedPost>
    suspend fun markShown(post: PostId, at: Instant)
    suspend fun markLess(post: PostId)
}
