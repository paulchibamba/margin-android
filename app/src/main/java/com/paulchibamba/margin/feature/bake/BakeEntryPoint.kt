package com.paulchibamba.margin.feature.bake

import com.paulchibamba.margin.domain.usecase.BakeProgressPosts
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@EntryPoint
@InstallIn(SingletonComponent::class)
interface BakeEntryPoint {
    fun bakeProgressPosts(): BakeProgressPosts
}
