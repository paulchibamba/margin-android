package com.paulchibamba.margin.domain.usecase

import com.paulchibamba.margin.domain.model.ChapterRef
import com.paulchibamba.margin.domain.repository.ProgressRepository
import javax.inject.Inject

class UnmarkChapterKnown @Inject constructor(private val progress: ProgressRepository) {

    suspend operator fun invoke(chapter: ChapterRef) {
        progress.unmarkChapterKnown(chapter)
    }
}
