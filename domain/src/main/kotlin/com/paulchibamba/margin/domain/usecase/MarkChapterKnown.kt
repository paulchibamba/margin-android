package com.paulchibamba.margin.domain.usecase

import com.paulchibamba.margin.domain.model.ChapterRef
import com.paulchibamba.margin.domain.repository.Clock
import com.paulchibamba.margin.domain.repository.ProgressRepository
import javax.inject.Inject

class MarkChapterKnown @Inject constructor(private val progress: ProgressRepository, private val clock: Clock) {

    suspend operator fun invoke(chapter: ChapterRef) {
        progress.markChapterKnown(chapter, clock.now())
    }
}
